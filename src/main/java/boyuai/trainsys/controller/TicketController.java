package boyuai.trainsys.controller;

import boyuai.trainsys.dto.*;
import boyuai.trainsys.service.TicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ticket")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    @PostMapping("/release")
    public ApiResponse<String> releaseTicket(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @RequestBody TicketQueryRequest request) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return ticketService.releaseTicket(sessionId.substring(7), request);
    }

    @PostMapping("/expire")
    public ApiResponse<String> expireTicket(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @RequestBody TicketQueryRequest request) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return ticketService.expireTicket(sessionId.substring(7), request);
    }

    @PostMapping("/remaining")
    public ApiResponse<Integer> queryRemainingTicket(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @RequestBody TicketQueryRequest request) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return ticketService.queryRemainingTicket(sessionId.substring(7), request);
    }

    @PostMapping("/buy")
    public ApiResponse<String> buyTicket(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @RequestBody BuyTicketRequest request) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return ticketService.buyTicket(sessionId.substring(7), request);
    }

    @PostMapping("/refund")
    public ApiResponse<String> refundTicket(
            @RequestHeader(value = "Authorization", required = false) String sessionId,
            @RequestBody RefundTicketRequest request) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return ticketService.refundTicket(sessionId.substring(7), request);
    }

    @GetMapping("/orders")
    public ApiResponse<List<TripInfoDTO>> queryMyOrders(
            @RequestHeader(value = "Authorization", required = false) String sessionId) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return ticketService.queryMyOrders(sessionId.substring(7));
    }

    @GetMapping("/list")
    public ApiResponse<List<TicketInfoDTO>> getTicketList(
            @RequestHeader(value = "Authorization", required = false) String sessionId) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return ticketService.getTicketList(sessionId.substring(7));
    }
}
