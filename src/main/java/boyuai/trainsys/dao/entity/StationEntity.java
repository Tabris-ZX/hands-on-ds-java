package boyuai.trainsys.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("station")
public class StationEntity {
    @TableId(value = "id", type = IdType.INPUT)
    private Integer id;
    private String name;
}
