package com.takeaway.dto;

import com.takeaway.entity.User;
import jakarta.persistence.Column;
import lombok.Data;

import java.util.Date;

/**
 * @author sunqichen
 * @version 0.1
 * @ClassName:
 * @Description:
 * @date
 * @since 0.1
 */
@Data
public class UserLoginDto {

    private String token;

    private User user;
}
