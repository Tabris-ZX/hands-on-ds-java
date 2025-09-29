package boyuai.trainsys.config;

/**
 * 系统配置常量
 */
public final class Config {

    private Config() {} // 防止实例化

    public static final int MAX_TRAINID_LEN = 20;
    public static final int MAX_USERNAME_LEN = 20;
    public static final int MAX_PASSWORD_LEN = 30;

    public static final int MAX_PASSING_STATION_NUMBER = 30;

    public static final int MAX_STATIONID = 1000;
    public static final int MAX_STATIONNAME_LEN = 30;

    public static final int ADMIN_PRIVILEGE = 10;

    public static final int BUSY_STATE_THRESHOLD = 1;

    public static final int MAX_STRING_LENGTH = 50;
}
