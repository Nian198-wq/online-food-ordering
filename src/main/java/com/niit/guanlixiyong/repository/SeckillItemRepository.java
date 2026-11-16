package com.niit.guanlixiyong.repository;

import com.niit.guanlixiyong.entity.SeckillItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 秒杀商品数据访问接口
 */
@Repository
public interface SeckillItemRepository extends JpaRepository<SeckillItem, Long> {
    
    /**
     * 根据活动ID查询秒杀商品列表
     */
    List<SeckillItem> findByActivityIdAndIsActiveTrue(Long activityId);
    
    /**
     * 根据活动ID和菜品ID查询
     */
    SeckillItem findByActivityIdAndDishes_Id(Long activityId, Long dishesId);
    
    /**
     * 查询所有进行中的秒杀商品
     */
    @Query("SELECT si FROM SeckillItem si WHERE si.activity.status = 'ONGOING' AND si.activity.isActive = true AND si.isActive = true AND si.stock > si.soldStock")
    List<SeckillItem> findOngoingSeckillItems();
    
    /**
     * 检查用户是否已购买过该秒杀商品
     */
    @Query("SELECT COUNT(o) FROM Order o JOIN o.orderItems oi JOIN oi.dishes d WHERE o.user.id = :userId AND d.id = :dishesId AND o.createTime >= :startTime AND o.createTime <= :endTime")
    long countUserSeckillOrders(@Param("userId") Long userId, @Param("dishesId") Long dishesId, @Param("startTime") java.time.LocalDateTime startTime, @Param("endTime") java.time.LocalDateTime endTime);
}
