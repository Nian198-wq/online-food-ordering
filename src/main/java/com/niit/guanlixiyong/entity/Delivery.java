package com.niit.guanlixiyong.entity;

import lombok.Data;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 配送实体类
 */
@Data
@Entity
@Table(name = "delivery")
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_user_id")
    private Users deliveryUser;

    @Column(nullable = false, length = 200)
    private String pickupAddress;

    @Column(nullable = false, length = 200)
    private String deliveryAddress;

    @Column(length = 20)
    private String deliveryPhone;

    @Column(nullable = false)
    private String status = "ASSIGNED"; // ASSIGNED, PICKED_UP, IN_TRANSIT, DELIVERED

    @Column
    private LocalDateTime pickupTime;

    @Column
    private LocalDateTime deliveredTime;

    @Column(nullable = false)
    private LocalDateTime assignedTime = LocalDateTime.now();

    @Column(length = 500)
    private String remark;
}
