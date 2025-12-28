package com.banking.payment;

import com.banking.payment.dto.PaymentRequest;
import com.banking.payment.dto.PaymentResponse;
import com.banking.payment.exception.PaymentNotFoundException;
import com.banking.payment.messaging.JmsProducer;
import com.banking.payment.model.Payment;
import com.banking.payment.repository.PaymentRepository;
import com.banking.payment.service.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org. junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit. jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private JmsProducer jmsProducer;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private PaymentRequest paymentRequest;
    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRequest = PaymentRequest.builder()
                .senderAccount("1234567890")
                .receiverAccount("0987654321")
                .amount(new BigDecimal("1000.00"))
                .currency("INR")
                .paymentMethod("UPI")
                .description("Test payment")
                .build();

        payment = Payment.builder()
                .id(1L)
                .transactionId("TXN-123")
                .senderAccount("1234567890")
                .receiverAccount("0987654321")
                .amount(new BigDecimal("1000.00"))
                .currency("INR")
                .status(Payment.PaymentStatus. PENDING)
                .build();
    }

    @Test
    void testCreatePayment_Success() {
        // Arrange
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);
        doNothing().when(jmsProducer).sendPaymentMessage(any(Payment.class));

        // Act
        PaymentResponse response = paymentService.createPayment(paymentRequest);

        // Assert
        assertNotNull(response);
        assertEquals("1234567890", response.getSenderAccount());
        assertEquals("0987654321", response.getReceiverAccount());
        assertEquals(new BigDecimal("1000.00"), response.getAmount());
        assertEquals(Payment.PaymentStatus.PENDING, response.getStatus());

        verify(paymentRepository, times(1)).save(any(Payment.class));
        verify(jmsProducer, times(1)).sendPaymentMessage(any(Payment.class));
    }

    @Test
    void testGetPaymentById_Success() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional. of(payment));

        // Act
        PaymentResponse response = paymentService.getPaymentById(1L);

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("TXN-123", response.getTransactionId());

        verify(paymentRepository, times(1)).findById(1L);
    }

    @Test
    void testGetPaymentById_NotFound() {
        // Arrange
        when(paymentRepository. findById(999L)).thenReturn(Optional. empty());

        // Act & Assert
        assertThrows(PaymentNotFoundException.class, () -> {
            paymentService.getPaymentById(999L);
        });

        verify(paymentRepository, times(1)).findById(999L);
    }

    @Test
    void testUpdatePaymentStatus_Success() {
        // Arrange
        when(paymentRepository.findById(1L)).thenReturn(Optional. of(payment));
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Act
        PaymentResponse response = paymentService.updatePaymentStatus(1L, Payment.PaymentStatus. COMPLETED);

        // Assert
        assertNotNull(response);
        verify(paymentRepository, times(1)).findById(1L);
        verify(paymentRepository, times(1)).save(any(Payment.class));
    }

    @Test
    void testDeletePayment_Success() {
        // Arrange
        when(paymentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(paymentRepository).deleteById(1L);

        // Act
        paymentService.deletePayment(1L);

        // Assert
        verify(paymentRepository, times(1)).existsById(1L);
        verify(paymentRepository, times(1)).deleteById(1L);
    }
}