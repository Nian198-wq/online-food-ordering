package com.niit.guanlixiyong.repository;

import com.niit.guanlixiyong.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 订单项数据访问接口
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * 按订单查找订单项
     */
    List<OrderItem> findByOrderId(Long orderId);
    
    /**
     * 按菜品查找订单项
     */
    List<OrderItem> findByDishesId(Long dishesId);
    
    /**
     * 统计最受欢迎菜品
     */
    @Query("SELECT oi.dishes.name, SUM(oi.quantity) as totalQuantity FROM OrderItem oi GROUP BY oi.dishes.id, oi.dishes.name ORDER BY totalQuantity DESC")
    List<Object[]> findTopSellingDishes();
}
