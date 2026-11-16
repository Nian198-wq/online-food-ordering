package com.niit.guanlixiyong.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 秒杀活动DTO
 */
@Data
public class SeckillActivityDTO {
    private Long id;
    private String name;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime startTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime endTime;
    
    private BigDecimal discount = BigDecimal.ONE; // 默认无折扣
    private String description;
    private String status;
    private Integer totalStock = 0; // 默认总库存为0
    private Integer soldStock;
    private Boolean isActive = true; // 默认激活
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
