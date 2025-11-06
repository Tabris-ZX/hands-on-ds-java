package boyuai.trainsys.dto;

import lombok.Data;

/**
 * 注册请求DTO
 */
@Data
public class RegisterRequest {
    private Long userId;
    private String username;
    private String password;
}

