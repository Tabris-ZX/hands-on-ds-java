package boyuai.trainsys.dto;

import lombok.Data;

/**
 * 登录请求DTO
 */
@Data
public class LoginRequest {
    private Long userId;
    private String password;
}

