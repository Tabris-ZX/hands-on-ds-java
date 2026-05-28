package boyuai.trainsys.dao;

import boyuai.trainsys.dao.entity.UserEntity;
import boyuai.trainsys.dao.mapper.UserMapper;
import boyuai.trainsys.dao.support.DbCodec;
import boyuai.trainsys.model.UserInfo;
import boyuai.trainsys.util.Types.UserID;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;

@Component
public class UserManager {
    private final UserMapper userMapper;

    public UserManager(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public void insertUser(UserID userID, String username, String password, int privilege) {
        UserEntity entity = new UserEntity();
        entity.setUserId(userID.value());
        entity.setUsername(username);
        entity.setPassword(password);
        entity.setPrivilege(privilege);
        userMapper.insert(entity);
    }

    public boolean existUser(UserID userID) {
        return userMapper.selectById(userID.value()) != null;
    }

    public UserInfo findUser(UserID userID) {
        UserEntity entity = userMapper.selectById(userID.value());
        if (entity == null) {
            return null;
        }
        return DbCodec.toUserInfo(entity.getUserId(), entity.getUsername(), entity.getPassword(), entity.getPrivilege());
    }

    public void removeUser(UserID userID) {
        userMapper.deleteById(userID.value());
    }

    public void modifyUserPrivilege(UserID userID, int newPrivilege) {
        UserEntity entity = userMapper.selectById(userID.value());
        if (entity != null) {
            entity.setPrivilege(newPrivilege);
            userMapper.updateById(entity);
        }
    }

    public void modifyUserPassword(UserID userID, String newPassword) {
        UserEntity entity = userMapper.selectById(userID.value());
        if (entity != null) {
            entity.setPassword(newPassword);
            userMapper.updateById(entity);
        }
    }

    public void close() {
    }
}
