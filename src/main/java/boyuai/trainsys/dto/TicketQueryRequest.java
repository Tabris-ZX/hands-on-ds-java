package boyuai.trainsys.dto;

import lombok.Data;

/**
 * 查询余票请求DTO
 */
@Data
public class TicketQueryRequest {
    private String trainId;
    private String departureTime;  // HH:MM MM-DD格式
    private String departureStation;  // 站点名称
}

