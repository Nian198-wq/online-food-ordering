package com.niit.guanlixiyong.repository;

import com.niit.guanlixiyong.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 支付数据访问接口
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    
    /**
     * 按订单查找支付
     */
    Optional<Payment> findByOrderId(Long orderId);
    
    /**
     * 按支付状态查找
     */
    List<Payment> findByStatus(String status);
    
    /**
     * 按支付方式统计
     */
    @Query("SELECT p.paymentMethod, COUNT(p) FROM Payment p GROUP BY p.paymentMethod")
    List<Object[]> countByPaymentMethod();
    
    /**
     * 统计各支付状态金额
     */
    @Query("SELECT p.status, SUM(p.amount) FROM Payment p GROUP BY p.status")
    List<Object[]> sumAmountByStatus();
}
