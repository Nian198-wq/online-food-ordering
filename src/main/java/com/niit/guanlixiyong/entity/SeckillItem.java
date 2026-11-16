package com.niit.guanlixiyong.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀商品实体类
 */
@Data
@Entity
@Table(name = "seckill_item")
public class SeckillItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "activity_id", nullable = false)
    private SeckillActivity activity; // 所属活动

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dishes_id", nullable = false)
    private Dishes dishes; // 关联菜品

    // 辅助方法：设置菜品ID
    public void setDishesId(Long dishesId) {
        if (this.dishes == null) {
            this.dishes = new Dishes();
        }
        this.dishes.setId(dishesId);
    }

    public Long getDishesId() {
        return this.dishes != null ? this.dishes.getId() : null;
    }

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal seckillPrice; // wadwd

    @Column(nullable = false)
    private Integer limitPerUser = 1; // 每人限购数量

    @Column(nullable = false)
    private Integer stock; // 该菜品库存

    @Column(nullable = false)
    private Integer soldStock = 0; // 已售库存

    @Column(nullable = false)
    private Boolean isActive = true; // 是否激活

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(nullable = false)
    private LocalDateTime createTime = java.time.LocalDateTime.now();

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(nullable = false)
    private LocalDateTime updateTime = java.time.LocalDateTime.now();
}
