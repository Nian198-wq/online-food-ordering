package com.niit.guanlixiyong.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.util.List;

/**
 * 创建订单请求 DTO
 */
@Data
public class CreateOrderRequest {

    @NotNull(message = "用户 ID 不能为空")
    private Long userId;

    @Size(max = 64, message = "客户端请求号长度不能超过64个字符")
    private String clientRequestId;

    @Size(max = 200, message = "配送地址长度不能超过200个字符")
    private String deliveryAddress; // 外卖地址（堂食时可为空）

    private String deliveryPhone; // 联系电话

    private String orderType = "DELIVERY"; // 订单类型：DELIVERY(外卖), DINE_IN(堂食)

    @Size(max = 200, message = "口味偏好长度不能超过200个字符")
    private String flavorPreference; // 口味偏好（如：微辣、加冰等）

    @NotNull(message = "订单项不能为空")
    @NotEmpty(message = "订单项不能为空")
    private List<CreateOrderItemDTO> items;

    @Size(max = 500, message = "备注长度不能超过500个字符")
    private String remarks;
}
