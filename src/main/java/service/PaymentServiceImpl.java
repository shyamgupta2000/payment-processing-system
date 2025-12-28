package com.banking.payment.service;

import com.banking.payment.dto.PaymentRequest;
import com.banking.payment.dto.PaymentResponse;
import com.banking.payment.exception.PaymentNotFoundException;
import com.banking.payment.messaging.JmsProducer;
import com.banking.payment.model.Payment;
import com.banking.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final JmsProducer jmsProducer;

    @Override
    public PaymentResponse createPayment(PaymentRequest request) {
        log.info("Creating payment from {} to {}", request.getSenderAccount(), request.getReceiverAccount());

        // Generate unique transaction ID
        String transactionId = "TXN-" + UUID.randomUUID().toString();

        // Build Payment entity
        Payment payment = Payment.builder()
                .transactionId(transactionId)
                .senderAccount(request.getSenderAccount())
                .receiverAccount(request.getReceiverAccount())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .paymentMethod(request.getPaymentMethod())
                .description(request.getDescription())
                .status(Payment.PaymentStatus. PENDING)
                .build();

        // Save to database
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with ID: {} and Transaction ID: {}", savedPayment.getId(), transactionId);

        // Send message to JMS queue for processing
        jmsProducer.sendPaymentMessage(savedPayment);

        return PaymentResponse.fromEntity(savedPayment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentById(Long id) {
        log.info("Fetching payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID:  " + id));
        return PaymentResponse.fromEntity(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponse getPaymentByTransactionId(String transactionId) {
        log.info("Fetching payment with Transaction ID: {}", transactionId);
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with Transaction ID: " + transactionId));
        return PaymentResponse.fromEntity(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getAllPayments() {
        log.info("Fetching all payments");
        return paymentRepository.findAll().stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsBySender(String senderAccount) {
        log.info("Fetching payments for sender:  {}", senderAccount);
        return paymentRepository.findBySenderAccount(senderAccount).stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByReceiver(String receiverAccount) {
        log.info("Fetching payments for receiver:  {}", receiverAccount);
        return paymentRepository.findByReceiverAccount(receiverAccount).stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPaymentsByStatus(Payment.PaymentStatus status) {
        log.info("Fetching payments with status: {}", status);
        return paymentRepository.findByStatus(status).stream()
                .map(PaymentResponse::fromEntity)
                .collect(Collectors. toList());
    }

    @Override
    public PaymentResponse updatePaymentStatus(Long id, Payment.PaymentStatus status) {
        log.info("Updating payment {} to status: {}", id, status);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new PaymentNotFoundException("Payment not found with ID: " + id));

        payment.setStatus(status);
        Payment updatedPayment = paymentRepository. save(payment);

        log.info("Payment {} status updated to {}", id, status);
        return PaymentResponse.fromEntity(updatedPayment);
    }

    @Override
    public void deletePayment(Long id) {
        log.info("Deleting payment with ID: {}", id);
        if (! paymentRepository.existsById(id)) {
            throw new PaymentNotFoundException("Payment not found with ID: " + id);
        }
        paymentRepository.deleteById(id);
        log.info("Payment {} deleted successfully", id);
    }
}