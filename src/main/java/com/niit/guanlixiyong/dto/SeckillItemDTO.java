package com.niit.guanlixiyong.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀商品DTO
 */
@Data
public class SeckillItemDTO {
    private Long id;
    private Long activityId;
    private Long dishesId;
    private String dishesName;
    private String dishesImageUrl;
    private BigDecimal originalPrice;
    private BigDecimal seckillPrice;
    private Integer limitPerUser;
    private Integer stock;
    private Integer soldStock;
    private Boolean isActive;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
