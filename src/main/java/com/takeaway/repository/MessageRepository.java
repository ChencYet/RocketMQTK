package com.takeaway.repository;

import com.takeaway.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByToUserIdOrderByCreateTimeDesc(Long toUserId);
    List<Message> findByFromUserIdOrderByCreateTimeDesc(Long fromUserId);
    List<Message> findByFromUserIdAndToUserIdOrFromUserIdAndToUserIdOrderByCreateTimeDesc(
            Long fromUserId1, Long toUserId1, Long fromUserId2, Long toUserId2);
}


