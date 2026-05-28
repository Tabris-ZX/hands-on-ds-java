package boyuai.trainsys.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("trip_info")
public class TripInfoEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String trainId;
    private Integer departureStation;
    private Integer arrivalStation;
    private Integer type;
    private Integer duration;
    private Integer price;
    private String departureTime;
    private String arrivalTime;
}
