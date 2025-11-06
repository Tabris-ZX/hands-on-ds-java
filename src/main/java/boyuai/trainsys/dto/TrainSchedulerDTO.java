package boyuai.trainsys.dto;

import lombok.Data;
import java.util.List;

/**
 * 车次调度计划DTO
 */
@Data
public class TrainSchedulerDTO {
    private String trainId;
    private Integer seatNum;
    private String startTime;
    private List<String> stations;  // 站点名称列表
    private List<Integer> durations;  // 区段时长列表（分钟）
    private List<Integer> prices;  // 区段票价列表
}

