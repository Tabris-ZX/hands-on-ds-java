package boyuai.trainsys.controller;

import boyuai.trainsys.dto.*;
import boyuai.trainsys.service.TrainSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 票务管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/ticket")
public class TicketController {
    
    @Autowired
    private TrainSystemService trainSystemService;
    
    @PostMapping("/release")
    public ApiResponse<String> releaseTicket(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @RequestBody TicketQueryRequest request) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainSystemService.releaseTicket(sessionId.substring(7), request);
    }
    
    @PostMapping("/expire")
    public ApiResponse<String> expireTicket(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @RequestBody TicketQueryRequest request) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainSystemService.expireTicket(sessionId.substring(7), request);
    }
    
    @PostMapping("/remaining")
    public ApiResponse<Integer> queryRemainingTicket(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @RequestBody TicketQueryRequest request) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainSystemService.queryRemainingTicket(sessionId.substring(7), request);
    }
    
    @PostMapping("/buy")
    public ApiResponse<String> buyTicket(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @RequestBody BuyTicketRequest request) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainSystemService.buyTicket(sessionId.substring(7), request);
    }
    
    @PostMapping("/refund")
    public ApiResponse<String> refundTicket(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @RequestBody RefundTicketRequest request) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainSystemService.refundTicket(sessionId.substring(7), request);
    }
    
    @GetMapping("/orders")
    public ApiResponse<List<TripInfoDTO>> queryMyOrders(
            @RequestHeader(value = "Authorization", required = false) String sessionId) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainSystemService.queryMyOrders(sessionId.substring(7));
    }
}

