package boyuai.trainsys.config;

/**
 * 系统配置常量类
 * <p>
 * 定义火车票务管理系统的全局配置参数，包括：
 * <ul>
 *   <li>字符串长度限制</li>
 *   <li>站点和车次配置</li>
 *   <li>权限级别</li>
 *   <li>系统状态阈值</li>
 * </ul>
 * <p>
 * 该类为 final 类，不可被继承，构造函数私有，不可实例化
 */
public final class StaticConfig {

    /** 私有构造函数，防止实例化 */
    private StaticConfig() {}

    /** 车次ID最大长度 */
    public static final int MAX_TRAINID_LEN = 20;
    
    /** 用户名最大长度 */
    public static final int MAX_USERNAME_LEN = 20;
    
    /** 密码最大长度 */
    public static final int MAX_PASSWORD_LEN = 30;

    /** 单趟列车最大经过站点数 */
    public static final int MAX_PASSING_STATION_NUMBER = 30;

    /** 系统支持的最大站点ID（用于初始化图结构和并查集） */
    public static final int MAX_STATIONID = 1000;
    
    /** 站点名称最大长度 */
    public static final int MAX_STATIONNAME_LEN = 30;

    /** 管理员权限等级 */
    public static final int ADMIN_PRIVILEGE = 10;

    /** 繁忙状态阈值（等待列表长度超过此值视为繁忙） */
    public static final int BUSY_STATE_THRESHOLD = 1;

    /** 通用字符串最大长度 */
    public static final int MAX_STRING_LENGTH = 50;

    public static final String DATABASE_PATH = "data/hands-on-ds.db";

    public static final String CONNECT_URL = "jdbc:sqlite:" + DATABASE_PATH;
}
