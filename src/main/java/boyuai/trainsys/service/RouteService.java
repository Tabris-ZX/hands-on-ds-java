package boyuai.trainsys.service;

import boyuai.trainsys.dao.RouteDao;
import boyuai.trainsys.model.ApiResponse;
import boyuai.trainsys.model.RouteQueryRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RouteService {

    private final RouteDao routeDao;

    public RouteService(RouteDao routeDao) {
        this.routeDao = routeDao;
    }

    public ApiResponse<String> findAllRoute(RouteQueryRequest request) {
        try {
            Integer depId = routeDao.stationNameToId(request.getDepartureStation());
            Integer arrId = routeDao.stationNameToId(request.getArrivalStation());
            if (depId == null || arrId == null) {
                return ApiResponse.error("站点不存在");
            }
            return ApiResponse.success(routeDao.findAllRoute(depId, arrId));
        } catch (Exception e) {
            return ApiResponse.error("查询路线失败: " + e.getMessage());
        }
    }

    public ApiResponse<String> findBestRoute(RouteQueryRequest request) {
        try {
            Integer depId = routeDao.stationNameToId(request.getDepartureStation());
            Integer arrId = routeDao.stationNameToId(request.getArrivalStation());
            if (depId == null || arrId == null) {
                return ApiResponse.error("站点不存在");
            }
            int preference = "price".equals(request.getPreference()) ? 0 : 1;
            return ApiResponse.success(routeDao.findBestRoute(depId, arrId, preference));
        } catch (Exception e) {
            return ApiResponse.error("查询最优路线失败: " + e.getMessage());
        }
    }

    public ApiResponse<Boolean> queryAccessibility(RouteQueryRequest request) {
        try {
            Integer depId = routeDao.stationNameToId(request.getDepartureStation());
            Integer arrId = routeDao.stationNameToId(request.getArrivalStation());
            if (depId == null || arrId == null) {
                return ApiResponse.error("站点不存在");
            }
            return ApiResponse.success(routeDao.queryAccessibility(depId, arrId));
        } catch (Exception e) {
            return ApiResponse.error("查询连通性失败: " + e.getMessage());
        }
    }

    public ApiResponse<List<String>> getAllStations() {
        try {
            return ApiResponse.success(routeDao.getAllStations());
        } catch (Exception e) {
            return ApiResponse.error("获取站点列表失败: " + e.getMessage());
        }
    }
}
