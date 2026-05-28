package boyuai.trainsys.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("train_scheduler")
public class TrainSchedulerEntity {
    @TableId(value = "train_id", type = IdType.INPUT)
    private String trainId;
    private Integer seatNum;
    private String startTime;
    private Integer passingNum;
    private String stations;
    private String duration;
    private String price;
}
