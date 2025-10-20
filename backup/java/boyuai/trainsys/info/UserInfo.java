package boyuai.trainsys.info;

import boyuai.trainsys.config.Config;
import boyuai.trainsys.util.Types.UserID;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * 用户信息类
 */
@Getter
@Setter
@Data
public class UserInfo implements Comparable<UserInfo> {
    // Getters and Setters
    private UserID userID;
    private String username;
    private String password;
    private int privilege;  // 数字越大，排队购票优先级越高

    /**
     * 默认构造函数
     */
    public UserInfo() {
    }

    /**
     * 构造函数
     * @param userID 用户ID
     * @param username 用户名
     * @param password 密码
     * @param privilege 权限级别
     */
    public UserInfo(UserID userID, String username, String password, int privilege) {
        this.userID = userID;
        setUsername(username);
        setPassword(password);
        this.privilege = privilege;
    }

    /**
     * 复制构造函数
     * @param other 要复制的UserInfo对象
     */
    public UserInfo(UserInfo other) {
        this.userID = other.userID;
        this.username = other.username;
        this.password = other.password;
        this.privilege = other.privilege;
    }

    public void setUsername(String username) {
        if (username == null) {
            this.username = "";
        } else if (username.length() > Config.MAX_USERNAME_LEN) {
            this.username = username.substring(0, Config.MAX_USERNAME_LEN);
        } else {
            this.username = username;
        }
    }

    public void setPassword(String password) {
        if (password == null) {
            this.password = "";
        } else if (password.length() > Config.MAX_PASSWORD_LEN) {
            this.password = password.substring(0, Config.MAX_PASSWORD_LEN);
        } else {
            this.password = password;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserInfo userInfo = (UserInfo) obj;
        return Objects.equals(userID, userInfo.userID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userID);
    }

    @Override
    public int compareTo(UserInfo other) {
        return Long.compare(this.userID.value(), other.userID.value());
    }
}
