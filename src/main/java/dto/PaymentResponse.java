package com.banking.payment.dto;

import com.banking.payment.model.Payment;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentResponse {

    private Long id;
    private String transactionId;
    private String senderAccount;
    private String receiverAccount;
    private BigDecimal amount;
    private String currency;
    private Payment.PaymentStatus status;
    private String paymentMethod;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Convert Entity to DTO
    public static PaymentResponse fromEntity(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .transactionId(payment.getTransactionId())
                .senderAccount(payment.getSenderAccount())
                .receiverAccount(payment. getReceiverAccount())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .paymentMethod(payment.getPaymentMethod())
                .description(payment.getDescription())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}