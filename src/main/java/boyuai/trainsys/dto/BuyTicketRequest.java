package boyuai.trainsys.dto;

import lombok.Data;

/**
 * 购票请求DTO
 */
@Data
public class BuyTicketRequest {
    private String trainId;
    private String departureTime;  // HH:MM MM-DD格式
    private String departureStation;  // 站点名称
}

