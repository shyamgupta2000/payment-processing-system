package com.banking.payment.controller;

import com.banking.payment. dto.PaymentRequest;
import com.banking.payment.dto. PaymentResponse;
import com. banking.payment.model.Payment;
import com.banking.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework. http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("Received payment request from {} to {}", request.getSenderAccount(), request.getReceiverAccount());
        PaymentResponse response = paymentService. createPayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> getPaymentById(@PathVariable Long id) {
        log.info("Fetching payment with ID: {}", id);
        PaymentResponse response = paymentService.getPaymentById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<PaymentResponse> getPaymentByTransactionId(@PathVariable String transactionId) {
        log.info("Fetching payment with transaction ID: {}", transactionId);
        PaymentResponse response = paymentService.getPaymentByTransactionId(transactionId);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        log.info("Fetching all payments");
        List<PaymentResponse> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/sender/{senderAccount}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsBySender(@PathVariable String senderAccount) {
        log.info("Fetching payments for sender: {}", senderAccount);
        List<PaymentResponse> payments = paymentService.getPaymentsBySender(senderAccount);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/receiver/{receiverAccount}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByReceiver(@PathVariable String receiverAccount) {
        log.info("Fetching payments for receiver: {}", receiverAccount);
        List<PaymentResponse> payments = paymentService.getPaymentsByReceiver(receiverAccount);
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<PaymentResponse>> getPaymentsByStatus(@PathVariable Payment.PaymentStatus status) {
        log.info("Fetching payments with status: {}", status);
        List<PaymentResponse> payments = paymentService.getPaymentsByStatus(status);
        return ResponseEntity.ok(payments);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PaymentResponse> updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam Payment.PaymentStatus status) {
        log.info("Updating payment {} status to {}", id, status);
        PaymentResponse response = paymentService.updatePaymentStatus(id, status);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        log.info("Deleting payment with ID: {}", id);
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
}