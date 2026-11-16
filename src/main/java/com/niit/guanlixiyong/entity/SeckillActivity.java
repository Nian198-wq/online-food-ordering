package com.niit.guanlixiyong.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀活动实体类
 */
@Data
@Entity
@Table(name = "seckill_activity")
public class SeckillActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // 活动名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(nullable = false)
    private LocalDateTime startTime; // 秒杀开始时间

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(nullable = false)
    private LocalDateTime endTime; // 秒杀结束时间

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal discount = BigDecimal.ONE; // 折扣(0.1-1.0，例如0.5表示5折)，默认无折扣

    @Column(length = 500)
    private String description; // 活动描述

    @Column(nullable = false)
    private String status = "UPCOMING"; // UPCOMING(即将开始), ONGOING(进行中), ENDED(已结束), CANCELLED(已取消)

    @Column(nullable = false)
    private Integer totalStock = 0; // 总库存，初始为0，添加商品时累加

    @Column(nullable = false)
    private Integer soldStock = 0; // 已售库存

    @Column(nullable = false)
    private Boolean isActive = true; // 活动是否激活

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(nullable = false)
    private LocalDateTime createTime = LocalDateTime.now();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(nullable = false)
    private LocalDateTime updateTime = LocalDateTime.now();
}
