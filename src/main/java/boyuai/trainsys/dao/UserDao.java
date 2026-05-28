package boyuai.trainsys.dao;

import boyuai.trainsys.core.TrainSystem;
import boyuai.trainsys.dto.LoginRequest;
import boyuai.trainsys.dto.RegisterRequest;
import boyuai.trainsys.info.UserInfo;
import org.springframework.stereotype.Repository;

@Repository
public class UserDao {

    private final SystemContextDao systemContextDao;

    public UserDao(SystemContextDao systemContextDao) {
        this.systemContextDao = systemContextDao;
    }

    public UserInfo login(LoginRequest request) {
        TrainSystem trainSystem = systemContextDao.getTrainSystem();
        trainSystem.login(request.getUserId(), request.getPassword());
        return trainSystem.getCurrentUser();
    }

    public void register(RegisterRequest request) {
        systemContextDao.getTrainSystem().addUser(
                request.getUserId(),
                request.getUsername(),
                request.getPassword()
        );
    }

    public void logout() {
        systemContextDao.getTrainSystem().logout();
    }
}
