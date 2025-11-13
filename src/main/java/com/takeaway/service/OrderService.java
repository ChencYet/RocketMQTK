package com.takeaway.service;

import com.takeaway.entity.Order;
import com.takeaway.entity.OrderItem;
import com.takeaway.entity.Food;
import com.takeaway.enums.UserType;
import com.takeaway.repository.OrderRepository;
import com.takeaway.repository.OrderItemRepository;
import com.takeaway.repository.FoodRepository;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private FoodRepository foodRepository;
    
    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    
    private static final String ORDER_TOPIC = "order-topic";
    
    @Transactional
    public Order createOrder(Long userId, List<OrderItem> items, String address, String phone) {
        // 生成订单号
        String orderNo = "ORD" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8);
        
        // 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : items) {
            Food food = foodRepository.findById(item.getFoodId())
                    .orElseThrow(() -> new RuntimeException("商品不存在"));
            
            // 检查库存
            if (food.getStock() < item.getQuantity()) {
                throw new RuntimeException("商品库存不足: " + food.getName());
            }
            
            item.setPrice(food.getPrice());
            item.setSubtotal(food.getPrice().multiply(new BigDecimal(item.getQuantity())));
            totalAmount = totalAmount.add(item.getSubtotal());
        }
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus("PENDING");
        order.setAddress(address);
        order.setPhone(phone);
        order = orderRepository.save(order);
        
        // 保存订单项
        for (OrderItem item : items) {
            item.setOrderId(order.getId());
            orderItemRepository.save(item);
            
            // 减少库存
            Food food = foodRepository.findById(item.getFoodId()).orElse(null);
            if (food != null) {
                food.setStock(food.getStock() - item.getQuantity());
                foodRepository.save(food);
            }
        }
        
        // 发送订单创建消息到RocketMQ
        rocketMQTemplate.convertAndSend(ORDER_TOPIC, order);
        
        return order;
    }
    
    public List<Order> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId);
    }
    
    public Order getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }
    
    @Transactional
    public Order updateOrderStatus(Long orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("订单不存在"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public static void main(String[] args) {

        System.out.println("usertype value = " + UserType.ADMIN.ordinal());

    }
}



