package com.takeaway.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author sunqichen
 * @version 0.1
 * @ClassName:
 * @Description:
 * @date
 * @since 0.1
 */
@Data
public class UserDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String address;

    private String phone;

}
