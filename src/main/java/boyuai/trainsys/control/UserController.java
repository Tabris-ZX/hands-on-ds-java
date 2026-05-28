package boyuai.trainsys.control;

import boyuai.trainsys.model.ApiResponse;
import boyuai.trainsys.model.LoginRequest;
import boyuai.trainsys.model.RegisterRequest;
import boyuai.trainsys.model.UserInfoDTO;
import boyuai.trainsys.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ApiResponse<Map<String, Object>> login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }

    @PostMapping("/register")
    public ApiResponse<String> register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/logout")
    public ApiResponse<String> logout(@RequestHeader(value = "Authorization", required = false) String sessionId) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return userService.logout(sessionId.substring(7));
    }

    @GetMapping("/validate")
    public ApiResponse<UserInfoDTO> validate(
            @RequestHeader(value = "Authorization", required = false) String sessionId) {
        if (sessionId == null || !sessionId.startsWith("Bearer ")) {
            return ApiResponse.error(401, "未登录");
        }
        return userService.validateSession(sessionId.substring(7));
    }
}
