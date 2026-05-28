package boyuai.trainsys.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ticket_info")
public class TicketInfoEntity {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String trainId;
    private String departureTime;
    private Integer departureStation;
    private Integer arrivalStation;
    private Integer seatNum;
    private Integer price;
    private Integer duration;
}
