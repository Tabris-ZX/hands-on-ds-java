package boyuai.trainsys.manager;

import boyuai.trainsys.datastructure.BPlusTree;
import boyuai.trainsys.datastructure.SeqList;
import boyuai.trainsys.info.UserInfo;
import boyuai.trainsys.util.Types.UserID;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理器
 */
public class UserManager {
    private BPlusTree<Long, UserInfo> userInfoTable;

    // 内存缓存，避免持久层延迟导致的查找问题
    private final Map<Long, UserInfo> cache = new HashMap<>();

    /**
     * 构造函数
     * @param filename 数据文件名
     */
    public UserManager(String filename) {
        userInfoTable = new BPlusTree<>(filename);
    }

    /**
     * 插入用户
     */
    public void insertUser(UserID userID, String username, String password, int privilege) {
        UserInfo userInfo = new UserInfo(userID, username, password, privilege);
        userInfoTable.insert(userID.value(), userInfo);
        cache.put(userID.value(), userInfo);
    }

    /**
     * 检查用户是否存在
     */
    public boolean existUser(UserID userID) {
        if (cache.containsKey(userID.value())) return true;
        SeqList<UserInfo> result = userInfoTable.find(userID.value());
        boolean exists = !result.Empty();
        if (exists) cache.put(userID.value(), result.visit(0));
        return exists;
    }

    /**
     * 查找用户
     */
    public UserInfo findUser(UserID userID) {
        if (cache.containsKey(userID.value())) {
            return cache.get(userID.value());
        }
        SeqList<UserInfo> result = userInfoTable.find(userID.value());
        if (!result.Empty()) {
            UserInfo user = result.visit(0);
            cache.put(userID.value(), user);
            return user;
        }
        return null;
    }

    /**
     * 删除用户
     */
    public void removeUser(UserID userID) {
        UserInfo user = findUser(userID);
        if (user != null) {
            userInfoTable.remove(userID.value(), user);
            cache.remove(userID.value());
        }
    }

    /**
     * 修改用户权限
     */
    public void modifyUserPrivilege(UserID userID, int newPrivilege) {
        UserInfo user = findUser(userID);
        if (user != null) {
            userInfoTable.remove(userID.value(), user);
            user.setPrivilege(newPrivilege);
            userInfoTable.insert(userID.value(), user);
            cache.put(userID.value(), user);
        }
    }

    /**
     * 修改用户密码
     */
    public void modifyUserPassword(UserID userID, String newPassword) {
        UserInfo user = findUser(userID);
        if (user != null) {
            userInfoTable.remove(userID.value(), user);
            user.setPassword(newPassword);
            userInfoTable.insert(userID.value(), user);
            cache.put(userID.value(), user);
        }
    }
}
