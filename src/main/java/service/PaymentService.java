package com.banking.payment.service;

import com.banking.payment.dto.PaymentRequest;
import com.banking.payment.dto.PaymentResponse;
import com.banking.payment.model.Payment;

import java.util.List;

public interface PaymentService {

    PaymentResponse createPayment(PaymentRequest request);

    PaymentResponse getPaymentById(Long id);

    PaymentResponse getPaymentByTransactionId(String transactionId);

    List<PaymentResponse> getAllPayments();

    List<PaymentResponse> getPaymentsBySender(String senderAccount);

    List<PaymentResponse> getPaymentsByReceiver(String receiverAccount);

    List<PaymentResponse> getPaymentsByStatus(Payment.PaymentStatus status);

    PaymentResponse updatePaymentStatus(Long id, Payment.PaymentStatus status);

    void deletePayment(Long id);
}