package boyuai.trainsys.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("station_component")
public class StationComponentEntity {
    @TableId(value = "station_id", type = IdType.INPUT)
    private Integer stationId;
    private Integer componentId;
}
