package boyuai.trainsys.dao;

import boyuai.trainsys.info.UserInfo;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SessionDao {

    private final Map<String, UserInfo> sessionMap = new ConcurrentHashMap<>();

    public void save(String sessionId, UserInfo userInfo) {
        sessionMap.put(sessionId, userInfo);
    }

    public UserInfo find(String sessionId) {
        return sessionMap.get(sessionId);
    }

    public void remove(String sessionId) {
        sessionMap.remove(sessionId);
    }
}
