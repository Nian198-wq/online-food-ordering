package com.niit.guanlixiyong.service;

import com.niit.guanlixiyong.dto.StatisticsDTO;
import com.niit.guanlixiyong.repository.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 统计分析服务类
 */
@Service
public class StatisticsService {

    @Resource
    private OrderRepository orderRepository;

    @Resource
    private PaymentRepository paymentRepository;

    @Resource
    private DishesRepository dishesRepository;

    @Resource
    private UsersRepository usersRepository;

    @Resource
    private OrderItemRepository orderItemRepository;

    @Resource
    private DeliveryRepository deliveryRepository;

    /**
     * 获取综合统计信息
     */
    public StatisticsDTO getOverview() {
        // 总订单数
        long totalOrders = orderRepository.count();
        
        // 待处理订单
        long pendingOrders = orderRepository.countByStatus().stream()
                .filter(r -> "PENDING".equals(r[0]))
                .count();
        
        // 已完成订单
        long completedOrders = orderRepository.countByStatus().stream()
                .filter(r -> "COMPLETED".equals(r[0]))
                .count();
        
        // 总收入
        BigDecimal totalRevenue = paymentRepository.sumAmountByStatus().stream()
                .filter(r -> "SUCCESS".equals(r[0]))
                .map(r -> (BigDecimal) r[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // 本月收入
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        BigDecimal monthlyRevenue = orderRepository.countMonthlyRevenue().stream()
                .filter(r -> {
                    Integer month = (Integer) r[0];
                    return month == LocalDate.now().getMonthValue();
                })
                .map(r -> (BigDecimal) r[1])
                .findFirst()
                .orElse(BigDecimal.ZERO);
        
        // 用户总数
        long totalUsers = usersRepository.count();
        
        // 菜品总数
        long totalDishes = dishesRepository.count();
        
        // 每日统计
        StatisticsDTO.DailyStats dailyStats = calculateDailyStats();
        
        // 最受欢迎菜品
        StatisticsDTO.TopDishes topDishes = getTopSellingDishes();
        
        return new StatisticsDTO(
                totalOrders,
                pendingOrders,
                completedOrders,
                totalRevenue.doubleValue(),
                monthlyRevenue.doubleValue(),
                totalUsers,
                totalDishes,
                dailyStats,
                topDishes
        );
    }

    /**
     * 计算每日统计
     */
    private StatisticsDTO.DailyStats calculateDailyStats() {
        LocalDate today = LocalDate.now();
        LocalDate weekAgo = today.minusWeeks(1);
        LocalDate monthAgo = today.minusMonths(1);
        
        LocalDateTime todayStart = today.atStartOfDay();
        LocalDateTime weekAgoStart = weekAgo.atStartOfDay();
        LocalDateTime monthAgoStart = monthAgo.atStartOfDay();
        
        List<Object[]> dailyOrders = orderRepository.countDailyOrders();
        
        int todayOrders = 0;
        int weekOrders = 0;
        int monthOrders = 0;
        
        for (Object[] result : dailyOrders) {
            LocalDate date = (LocalDate) result[0];
            Long count = (Long) result[1];
            
            if (date.equals(today)) {
                todayOrders = count.intValue();
            }
            if (!date.isBefore(weekAgo)) {
                weekOrders += count.intValue();
            }
            if (!date.isBefore(monthAgo)) {
                monthOrders += count.intValue();
            }
        }
        
        return new StatisticsDTO.DailyStats(todayOrders, weekOrders, monthOrders);
    }

    /**
     * 获取最受欢迎菜品
     */
    private StatisticsDTO.TopDishes getTopSellingDishes() {
        List<Object[]> topSelling = orderItemRepository.findTopSellingDishes();
        if (topSelling.isEmpty()) {
            return new StatisticsDTO.TopDishes("无", 0L);
        }
        Object[] top = topSelling.get(0);
        return new StatisticsDTO.TopDishes((String) top[0], (Long) top[1]);
    }

    /**
     * 获取订单状态统计
     */
    public Map<String, Long> getOrderStatusStatistics() {
        Map<String, Long> stats = new HashMap<>();
        List<Object[]> results = orderRepository.countByStatus();
        for (Object[] result : results) {
            stats.put((String) result[0], (Long) result[1]);
        }
        return stats;
    }

    /**
     * 获取支付方式统计
     */
    public Map<String, Long> getPaymentMethodStatistics() {
        Map<String, Long> stats = new HashMap<>();
        List<Object[]> results = paymentRepository.countByPaymentMethod();
        for (Object[] result : results) {
            stats.put((String) result[0], (Long) result[1]);
        }
        return stats;
    }

    /**
     * 获取菜品类别统计
     */
    public Map<String, Long> getCategoryStatistics() {
        Map<String, Long> stats = new HashMap<>();
        List<Object[]> results = dishesRepository.countByCategory();
        for (Object[] result : results) {
            stats.put((String) result[0], ((Number) result[1]).longValue());
        }
        return stats;
    }

    /**
     * 获取用户角色统计
     */
    public Map<String, Long> getUserRoleStatistics() {
        Map<String, Long> stats = new HashMap<>();
        List<Object[]> results = usersRepository.countByRole();
        for (Object[] result : results) {
            stats.put((String) result[0], (Long) result[1]);
        }
        return stats;
    }

    /**
     * 获取配送状态统计
     */
    public Map<String, Long> getDeliveryStatusStatistics() {
        Map<String, Long> stats = new HashMap<>();
        List<Object[]> results = deliveryRepository.countByStatus();
        for (Object[] result : results) {
            stats.put((String) result[0], (Long) result[1]);
        }
        return stats;
    }

    /**
     * 获取月度收入统计
     */
    public Map<Integer, BigDecimal> getMonthlyRevenueStatistics() {
        Map<Integer, BigDecimal> stats = new HashMap<>();
        List<Object[]> results = orderRepository.countMonthlyRevenue();
        for (Object[] result : results) {
            stats.put((Integer) result[0], (BigDecimal) result[1]);
        }
        return stats;
    }

    /**
     * 获取配送员绩效统计
     */
    public Map<String, Long> getDeliveryUserPerformance() {
        Map<String, Long> stats = new HashMap<>();
        List<Object[]> results = deliveryRepository.countDeliveriesByUser();
        for (Object[] result : results) {
            stats.put((String) result[0], (Long) result[1]);
        }
        return stats;
    }
}
