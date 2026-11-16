package com.niit.guanlixiyong.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 订单项 DTO
 */
@Data
public class OrderItemDTO {

    private Long dishesId;
    private Long orderId;
    private String dishesName;
    private java.math.BigDecimal price;
    private Integer quantity;
    private java.math.BigDecimal subtotal;
    private String specialRequest;
}
