package com.niit.guanlixiyong.dto;

import lombok.Data;

/**
 * 支付请求 DTO
 */
@Data
public class PaymentRequest {
    private Long orderId;
    private String paymentMethod; // ALIPAY, WECHAT, CARD, CASH
}
