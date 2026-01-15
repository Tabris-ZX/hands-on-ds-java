package boyuai.trainsys.controller;

import boyuai.trainsys.dto.*;
import boyuai.trainsys.service.TrainSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车次管理控制器（管理员）
 */
@Slf4j
@RestController
@RequestMapping("/api/train")
public class TrainController {
    
    @Autowired
    private TrainSystemService trainSystemService;
    
    @PostMapping("/add")
    public ApiResponse<String> addTrain(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @RequestBody AddTrainRequest request) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainSystemService.addTrain(sessionId.substring(7), request);
    }
    
    @GetMapping("/query/{trainId}")
    public ApiResponse<TrainSchedulerDTO> queryTrain(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @PathVariable("trainId") String trainId) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainSystemService.queryTrain(sessionId.substring(7), trainId);
    }
    
    @GetMapping("/list")
    public ApiResponse<List<TrainSchedulerDTO>> getAllTrains(
            @RequestHeader(value = "Authorization", required = false) String sessionId) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainSystemService.getAllTrainSchedulers(sessionId.substring(7));
    }
}

