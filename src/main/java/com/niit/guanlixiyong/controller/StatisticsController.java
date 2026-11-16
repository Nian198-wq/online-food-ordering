package com.niit.guanlixiyong.controller;

import com.niit.guanlixiyong.dto.StatisticsDTO;
import com.niit.guanlixiyong.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 统计分析控制器
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Resource
    private StatisticsService statisticsService;

    /**
     * 获取综合统计信息
     */
    @GetMapping("/overview")
    public ResponseEntity<StatisticsDTO> getOverview() {
        return ResponseEntity.ok(statisticsService.getOverview());
    }

    /**
     * 获取订单状态统计
     */
    @GetMapping("/orders/status")
    public ResponseEntity<Map<String, Long>> getOrderStatusStatistics() {
        return ResponseEntity.ok(statisticsService.getOrderStatusStatistics());
    }

    /**
     * 获取支付方式统计
     */
    @GetMapping("/payments/method")
    public ResponseEntity<Map<String, Long>> getPaymentMethodStatistics() {
        return ResponseEntity.ok(statisticsService.getPaymentMethodStatistics());
    }

    /**
     * 获取菜品类别统计
     */
    @GetMapping("/dishes/category")
    public ResponseEntity<Map<String, Long>> getCategoryStatistics() {
        return ResponseEntity.ok(statisticsService.getCategoryStatistics());
    }

    /**
     * 获取用户角色统计
     */
    @GetMapping("/users/role")
    public ResponseEntity<Map<String, Long>> getUserRoleStatistics() {
        return ResponseEntity.ok(statisticsService.getUserRoleStatistics());
    }

    /**
     * 获取配送状态统计
     */
    @GetMapping("/deliveries/status")
    public ResponseEntity<Map<String, Long>> getDeliveryStatusStatistics() {
        return ResponseEntity.ok(statisticsService.getDeliveryStatusStatistics());
    }

    /**
     * 获取月度收入统计
     */
    @GetMapping("/revenue/monthly")
    public ResponseEntity<Map<Integer, BigDecimal>> getMonthlyRevenueStatistics() {
        return ResponseEntity.ok(statisticsService.getMonthlyRevenueStatistics());
    }

    /**
     * 获取配送员绩效统计
     */
    @GetMapping("/delivery/performance")
    public ResponseEntity<Map<String, Long>> getDeliveryUserPerformance() {
        return ResponseEntity.ok(statisticsService.getDeliveryUserPerformance());
    }
}
