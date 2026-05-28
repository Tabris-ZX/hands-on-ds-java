package boyuai.trainsys.service;

import boyuai.trainsys.dao.SessionDao;
import boyuai.trainsys.dao.SystemContextDao;
import boyuai.trainsys.dao.UserDao;
import boyuai.trainsys.model.ApiResponse;
import boyuai.trainsys.model.LoginRequest;
import boyuai.trainsys.model.RegisterRequest;
import boyuai.trainsys.model.UserInfoDTO;
import boyuai.trainsys.model.UserInfo;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserDao userDao;
    private final SessionDao sessionDao;
    private final SystemContextDao systemContextDao;

    public UserService(UserDao userDao, SessionDao sessionDao, SystemContextDao systemContextDao) {
        this.userDao = userDao;
        this.sessionDao = sessionDao;
        this.systemContextDao = systemContextDao;
    }

    public ApiResponse<Map<String, Object>> login(LoginRequest request) {
        try {
            UserInfo currentUser = userDao.login(request);
            if (currentUser == null || currentUser.getUserID().value() == -1) {
                return ApiResponse.error("登录失败，用户ID或密码错误");
            }

            String sessionId = "session_" + currentUser.getUserID().value() + "_" + System.currentTimeMillis();
            sessionDao.save(sessionId, currentUser);

            Map<String, Object> result = new HashMap<>();
            result.put("sessionId", sessionId);
            result.put("user", toUserDTO(currentUser));
            return ApiResponse.success("登录成功", result);
        } catch (Exception e) {
            return ApiResponse.error("登录失败: " + e.getMessage());
        }
    }

    public ApiResponse<String> register(RegisterRequest request) {
        try {
            userDao.register(request);
            return ApiResponse.success("注册成功", null);
        } catch (Exception e) {
            return ApiResponse.error("注册失败: " + e.getMessage());
        }
    }

    public ApiResponse<String> logout(String sessionId) {
        sessionDao.remove(sessionId);
        userDao.logout();
        return ApiResponse.success("退出登录成功", null);
    }

    public UserInfo getCurrentUser(String sessionId) {
        return sessionDao.find(sessionId);
    }

    public void bindCurrentUser(String sessionId) {
        UserInfo user = getCurrentUser(sessionId);
        systemContextDao.getTrainSystem().setCurrentUser(user);
    }

    public ApiResponse<UserInfoDTO> validateSession(String sessionId) {
        UserInfo user = getCurrentUser(sessionId);
        if (user == null) {
            return ApiResponse.error(401, "Session expired");
        }
        return ApiResponse.success(toUserDTO(user));
    }

    private UserInfoDTO toUserDTO(UserInfo user) {
        UserInfoDTO userDTO = new UserInfoDTO();
        userDTO.setUserId(user.getUserID().value());
        userDTO.setUsername(user.getUsername());
        userDTO.setPrivilege(user.getPrivilege());
        return userDTO;
    }
}
