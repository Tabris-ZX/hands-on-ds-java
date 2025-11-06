package boyuai.trainsys.dto;

import lombok.Data;

/**
 * 路线查询请求DTO
 */
@Data
public class RouteQueryRequest {
    private String departureStation;  // 起点站名称
    private String arrivalStation;  // 终点站名称
    private String preference;  // "time" 或 "price"
}

