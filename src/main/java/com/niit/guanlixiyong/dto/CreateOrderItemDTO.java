package com.niit.guanlixiyong.dto;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 创建订单时的订单项 DTO
 */
@Data
public class CreateOrderItemDTO {

    @NotNull(message = "菜品 ID 不能为空")
    private Long dishesId;

    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量至少为 1")
    private Integer quantity;

    private String specialRequest;
}
