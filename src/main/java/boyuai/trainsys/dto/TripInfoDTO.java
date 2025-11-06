package boyuai.trainsys.dto;

import lombok.Data;

/**
 * 行程信息DTO
 */
@Data
public class TripInfoDTO {
    private String trainId;
    private String departureStation;
    private String arrivalStation;
    private Integer ticketNumber;
    private Integer duration;  // 分钟
    private Integer price;
    private String departureTime;  // HH:MM MM-DD格式
    private String arrivalTime;  // HH:MM MM-DD格式
}

