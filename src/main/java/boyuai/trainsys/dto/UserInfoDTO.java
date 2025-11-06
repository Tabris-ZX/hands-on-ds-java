package boyuai.trainsys.dto;

import lombok.Data;

/**
 * 用户信息DTO
 */
@Data
public class UserInfoDTO {
    private Long userId;
    private String username;
    private Integer privilege;
}

