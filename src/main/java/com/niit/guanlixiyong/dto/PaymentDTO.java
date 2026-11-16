package com.niit.guanlixiyong.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 支付 DTO
 */
@Data
public class PaymentDTO {
    private Long id;
    private String paymentMethod;
    private BigDecimal amount;
    private String status;
    private String transactionId;
    private LocalDateTime paymentTime;
}
