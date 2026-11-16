package com.niit.guanlixiyong.entity;

import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单实体类
 */
@Data
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String orderNo;

    @Column(unique = true, length = 64)
    private String clientRequestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(nullable = false)
    private String status = "PENDING"; // PENDING, CONFIRMED, PREPARING, DELIVERING, COMPLETED, CANCELLED

    @Column(nullable = false)
    private String orderType = "DELIVERY"; // DELIVERY(外卖), DINE_IN(堂食)

    @Column(nullable = false, length = 200)
    private String deliveryAddress; // 外卖地址（堂食时为空）

    @Column(length = 20)
    private String deliveryPhone; // 联系电话

    @Column(length = 200)
    private String flavorPreference; // 口味偏好（如：微辣、加冰等）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_user_id")
    private Users deliveryUser;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Payment payment;

    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @Column
    private LocalDateTime confirmTime;

    @Column
    private LocalDateTime prepareTime;

    @Column
    private LocalDateTime deliverTime;

    @Column
    private LocalDateTime completeTime;

    @Column(length = 500)
    private String remarks;
}
