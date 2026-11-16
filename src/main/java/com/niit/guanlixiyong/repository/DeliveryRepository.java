package com.niit.guanlixiyong.repository;

import com.niit.guanlixiyong.entity.Delivery;
import com.niit.guanlixiyong.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 配送数据访问接口
 */
@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    
    /**
     * 按订单查找配送
     */
    Optional<Delivery> findByOrderId(Long orderId);
    
    /**
     * 按配送员查找配送
     */
    List<Delivery> findByDeliveryUser(Users deliveryUser);
    
    /**
     * 按配送状态查找
     */
    List<Delivery> findByStatus(String status);
    
    /**
     * 统计各状态配送数量
     */
    @Query("SELECT d.status, COUNT(d) FROM Delivery d GROUP BY d.status")
    List<Object[]> countByStatus();
    
    /**
     * 统计配送员配送次数
     */
    @Query("SELECT d.deliveryUser.username, COUNT(d) FROM Delivery d WHERE d.deliveryUser IS NOT NULL GROUP BY d.deliveryUser.id, d.deliveryUser.username ORDER BY COUNT(d) DESC")
    List<Object[]> countDeliveriesByUser();
}
