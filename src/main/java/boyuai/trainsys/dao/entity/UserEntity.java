package boyuai.trainsys.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_info")
public class UserEntity {
    @TableId(value = "user_id", type = IdType.INPUT)
    private Long userId;
    private String username;
    private String password;
    private Integer privilege;
}
