package boyuai.trainsys.control;

import boyuai.trainsys.model.AddTrainRequest;
import boyuai.trainsys.model.ApiResponse;
import boyuai.trainsys.model.TrainSchedulerDTO;
import boyuai.trainsys.service.TrainService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/train")
public class TrainController {

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @PostMapping("/add")
    public ApiResponse<String> addTrain(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @RequestBody AddTrainRequest request) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainService.addTrain(sessionId.substring(7), request);
    }

    @GetMapping("/query/{trainId}")
    public ApiResponse<TrainSchedulerDTO> queryTrain(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @PathVariable("trainId") String trainId) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainService.queryTrain(sessionId.substring(7), trainId);
    }

    @GetMapping("/list")
    public ApiResponse<List<TrainSchedulerDTO>> getAllTrains(
            @RequestHeader(value = "Authorization", required = false) String sessionId) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainService.getAllTrainSchedulers(sessionId.substring(7));
    }
}
