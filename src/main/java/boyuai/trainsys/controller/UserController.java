package boyuai.trainsys.controller;

import boyuai.trainsys.dto.*;
import boyuai.trainsys.service.TrainSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private TrainSystemService trainSystemService;
    
    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return trainSystemService.login(request);
    }
    
    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {
        return trainSystemService.register(request);
    }
    
    @PostMapping("/logout")
    public ApiResponse<String> logout(@RequestHeader(value = "Authorization", required = false) String sessionId) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return trainSystemService.logout(sessionId.substring(7));
    }
}

