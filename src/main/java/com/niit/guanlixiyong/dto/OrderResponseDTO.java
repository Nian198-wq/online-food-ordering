package com.niit.guanlixiyong.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单响应 DTO
 */
@Data
public class OrderResponseDTO {
    private Long id;
    private String orderNo;
    private String clientRequestId;
    private Long userId;
    private String username;
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;
    private String status;
    private String orderType; // DELIVERY(外卖), DINE_IN(堂食)
    private String deliveryAddress;
    private String deliveryPhone;
    private String flavorPreference; // 口味偏好（如：微辣、加冰等）
    private Long deliveryUserId;
    private String deliveryUsername;
    private PaymentDTO payment;
    private LocalDateTime createTime;
    private LocalDateTime confirmTime;
    private LocalDateTime prepareTime;
    private LocalDateTime deliverTime;
    private LocalDateTime completeTime;
    private String remarks;
}
