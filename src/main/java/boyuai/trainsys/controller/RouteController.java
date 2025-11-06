package boyuai.trainsys.controller;

import boyuai.trainsys.dto.*;
import boyuai.trainsys.service.TrainSystemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 路线查询控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/route")
public class RouteController {
    
    @Autowired
    private TrainSystemService trainSystemService;
    
    @PostMapping("/findAll")
    public ApiResponse<String> findAllRoute(@RequestBody RouteQueryRequest request) {
        return trainSystemService.findAllRoute(request);
    }
    
    @PostMapping("/best")
    public ApiResponse<String> findBestRoute(@RequestBody RouteQueryRequest request) {
        return trainSystemService.findBestRoute(request);
    }
    
    @PostMapping("/accessibility")
    public ApiResponse<Boolean> queryAccessibility(@RequestBody RouteQueryRequest request) {
        return trainSystemService.queryAccessibility(request);
    }
    
    @GetMapping("/stations")
    public ApiResponse<List<String>> getAllStations() {
        return trainSystemService.getAllStations();
    }
}

