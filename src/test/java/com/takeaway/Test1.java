package com.takeaway;

import com.takeaway.dto.UserDto;
import com.takeaway.entity.User;
import com.takeaway.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

/**
 * @author sunqichen
 * @version 0.1
 * @ClassName:
 * @Description:
 * @date
 * @since 0.1
 */
//@SpringBootTest
public class Test1 {

    private UserMapper userMapper = Mappers.getMapper(UserMapper.class);


    @Test
    public void test() {

        User user = new User();
        user.setUsername("sunqichen");
        user.setPassword("123456");
        user.setPhone("12345678901");
        user.setAddress("中国");
        user.setUpdateTime(new Date());
        user.setCreateTime(new Date());
        user.setDel(0);
        UserDto userDto = userMapper.toDTO(user);
        System.out.printf("userDto: %s", userDto);

    }
}
