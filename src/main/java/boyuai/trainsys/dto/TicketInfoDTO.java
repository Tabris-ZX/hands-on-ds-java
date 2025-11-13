package boyuai.trainsys.dto;

import lombok.Data;

/**
 * 车票信息DTO
 */
@Data
public class TicketInfoDTO {
    private String trainId;
    private String departureStation;
    private String arrivalStation;
    private Integer seatNum;  // 余票数量
    private Integer price;
    private Integer duration;  // 运行时长（分钟）
    private String departureTime;  // HH:MM MM-DD格式
}


