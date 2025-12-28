package com.banking.payment.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequest {

    @NotBlank(message = "Sender account is required")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "Invalid sender account format")
    private String senderAccount;

    @NotBlank(message = "Receiver account is required")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "Invalid receiver account format")
    private String receiverAccount;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @DecimalMax(value = "1000000.00", message = "Amount exceeds maximum limit")
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Pattern(regexp = "^(INR|USD|EUR|GBP)$", message = "Invalid currency")
    private String currency;

    private String paymentMethod;

    private String description;
}