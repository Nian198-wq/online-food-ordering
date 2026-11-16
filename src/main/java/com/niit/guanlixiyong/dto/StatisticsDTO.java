package com.niit.guanlixiyong.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统计信息 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatisticsDTO {
    private Long totalOrders;
    private Long pendingOrders;
    private Long completedOrders;
    private Double totalRevenue;
    private Double monthlyRevenue;
    private Long totalUsers;
    private Long totalDishes;
    private DailyStats dailyStats;
    private TopDishes topDishes;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DailyStats {
        private Integer todayOrders;
        private Integer weekOrders;
        private Integer monthOrders;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopDishes {
        private String dishName;
        private Long totalSold;
    }
}
