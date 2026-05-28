package boyuai.trainsys.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("route_section")
public class RouteSectionEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String trainId;
    private Integer departureId;
    private Integer arrivalId;
    private Integer price;
    private Integer duration;
}
