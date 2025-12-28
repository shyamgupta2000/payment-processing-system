package com.banking.payment.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate. annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType. IDENTITY)
    private Long id;

    @NotBlank(message = "Transaction ID is required")
    @Column(nullable = false, unique = true, length = 50)
    private String transactionId;

    @NotBlank(message = "Sender account is required")
    @Column(nullable = false, length = 20)
    private String senderAccount;

    @NotBlank(message = "Receiver account is required")
    @Column(nullable = false, length = 20)
    private String receiverAccount;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @NotBlank(message = "Currency is required")
    @Column(nullable = false, length = 3)
    private String currency;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PaymentStatus status;

    @Column(length = 50)
    private String paymentMethod;

    @Column(length = 500)
    private String description;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Payment Status Enum
    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        CANCELLED
    }
}