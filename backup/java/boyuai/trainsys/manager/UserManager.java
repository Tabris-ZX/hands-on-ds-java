package boyuai.trainsys.manager;

import boyuai.trainsys.datastructure.BPlusTree;
import boyuai.trainsys.datastructure.SeqList;
import boyuai.trainsys.info.UserInfo;
import boyuai.trainsys.util.Types.UserID;

/**
 * 用户管理器
 */
public class UserManager {
    private BPlusTree<Long, UserInfo> userInfoTable;

    /**
     * 构造函数
     * @param filename 数据文件名
     */
    public UserManager(String filename) {
        // 使用UserID的值作为键
        userInfoTable = new BPlusTree<>(filename);
    }

    /**
     * 插入用户
     * @param userID 用户ID
     * @param username 用户名
     * @param password 密码
     * @param privilege 权限级别
     */
    public void insertUser(UserID userID, String username, String password, int privilege) {
        UserInfo userInfo = new UserInfo(userID, username, password, privilege);
        userInfoTable.insert(userID.value(), userInfo);
    }

    /**
     * 检查用户是否存在
     * @param userID 用户ID
     * @return 如果存在返回true
     */
    public boolean existUser(UserID userID) {
        SeqList<UserInfo> result = userInfoTable.find(userID.value());
        return !result.Empty();
    }

    /**
     * 查找用户
     * @param userID 用户ID
     * @return 用户信息，如果不存在返回null
     */
    public UserInfo findUser(UserID userID) {
        SeqList<UserInfo> result = userInfoTable.find(userID.value());
        if (!result.Empty()) {
            return result.visit(0);
        }
        return null;
    }

    /**
     * 删除用户
     * @param userID 用户ID
     */
    public void removeUser(UserID userID) {
        UserInfo user = findUser(userID);
        if (user != null) {
            userInfoTable.remove(userID.value(), user);
        }
    }

    /**
     * 修改用户权限
     * @param userID 用户ID
     * @param newPrivilege 新权限级别
     */
    public void modifyUserPrivilege(UserID userID, int newPrivilege) {
        UserInfo user = findUser(userID);
        if (user != null) {
            // 先删除旧的
            userInfoTable.remove(userID.value(), user);
            // 修改权限
            user.setPrivilege(newPrivilege);
            // 重新插入
            userInfoTable.insert(userID.value(), user);
        }
    }

    /**
     * 修改用户密码
     * @param userID 用户ID
     * @param newPassword 新密码
     */
    public void modifyUserPassword(UserID userID, String newPassword) {
        UserInfo user = findUser(userID);
        if (user != null) {
            // 先删除旧的
            userInfoTable.remove(userID.value(), user);
            // 修改密码
            user.setPassword(newPassword);
            // 重新插入
            userInfoTable.insert(userID.value(), user);
        }
    }

    /**
     * 关闭管理器（保存数据）
     */
    public void close() {
        userInfoTable.close();
    }
}
