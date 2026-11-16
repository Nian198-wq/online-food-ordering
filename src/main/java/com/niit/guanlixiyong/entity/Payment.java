package com.niit.guanlixiyong.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付实体类
 */
@Data
@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @Column(nullable = false, length = 50)
    private String paymentMethod; // ALIPAY, WECHAT, CARD, CASH

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, SUCCESS, FAILED, REFUNDED

    @Column
    private String transactionId;

    @Column(nullable = false)
    private LocalDateTime paymentTime = LocalDateTime.now();

    @Column(length = 500)
    private String remark;
}
