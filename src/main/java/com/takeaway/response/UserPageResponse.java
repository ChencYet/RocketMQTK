package com.takeaway.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class UserPageResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 响应代码
     */
    @Schema(description = "响应代码")
    private Integer code;

    /**
     * 响应消息
     */
    @Schema(description = "用户列表")
    private List<T> data;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Long total;

    /**
     * 响应消息
     */
    @Schema(description = "响应消息")
    private String message;

    // 构造方法
    public UserPageResponse(Integer code, String message, List<T> data, Long total) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.total = total;
    }

    public static <T> UserPageResponse success(List<T> data) {
        return new UserPageResponse<>(HttpStatus.OK.value(), "查询成功", data, Long.valueOf(data.size()));
    }

    public static UserPageResponse error() {
        return new UserPageResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "查询失败", List.of(), 0L);
    }
}
