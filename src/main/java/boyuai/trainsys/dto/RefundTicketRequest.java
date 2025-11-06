package boyuai.trainsys.dto;

import lombok.Data;

/**
 * 退票请求DTO
 */
@Data
public class RefundTicketRequest {
    private String trainId;
    private String departureTime;  // HH:MM MM-DD格式
    private String departureStation;  // 站点名称
}

