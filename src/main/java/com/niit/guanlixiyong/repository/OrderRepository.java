package com.niit.guanlixiyong.repository;

import com.niit.guanlixiyong.entity.Order;
import com.niit.guanlixiyong.entity.Users;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


/**
 * 订单数据访问接口
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * 按订单号查找
     */
    Optional<Order> findByOrderNo(String orderNo);

    /**
     * 按客户端请求号查找订单，用于提交幂等控制。
     */
    Optional<Order> findByClientRequestId(String clientRequestId);

    /**
     * 按用户查找订单
     */
    List<Order> findByUser(Users user);

    /**
     * 按用户和状态查找订单
     */
    List<Order> findByUserAndStatus(Users user, String status);

    /**
     * 按状态查找订单
     */
    List<Order> findByStatus(String status);

    /**
     * 分页按状态查找订单
     */
    Page<Order> findByStatus(String status, Pageable pageable);

    /**
     * 按配送员查找订单
     */
    List<Order> findByDeliveryUser(Users deliveryUser);

    /**
     * 按创建时间范围查询订单
     */
    @Query("SELECT o FROM Order o WHERE o.createTime BETWEEN :startTime AND :endTime")
    List<Order> findByCreateTimeBetween(@Param("startTime") LocalDateTime startTime,
                                        @Param("endTime") LocalDateTime endTime);

    /**
     * 统计各状态订单数量
     */
    @Query("SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status")
    List<Object[]> countByStatus();

    /**
     * 统计每日订单数量
     * 修改说明：
     * 1. 添加 nativeQuery = true 使用原生 SQL
     * 2. 将实体属性 o.createTime 改为数据库字段 o.create_time
     * 3. 将实体名 Order 改为数据库表名 orders
     */
    @Query(value = "SELECT DATE(create_time), COUNT(*) FROM orders GROUP BY DATE(create_time) ORDER BY DATE(create_time)", nativeQuery = true)
    List<Object[]> countDailyOrders();

    /**
     * 统计每月订单收入
     */
    @Query("SELECT MONTH(o.createTime), SUM(o.totalAmount) FROM Order o WHERE o.status NOT IN ('CANCELLED') GROUP BY MONTH(o.createTime) ORDER BY MONTH(o.createTime)")
    List<Object[]> countMonthlyRevenue();
}
