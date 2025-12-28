package com.banking.payment.repository;

import com.banking.payment.model.Payment;
import org.springframework.data.jpa.repository. JpaRepository;
import org. springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);

    List<Payment> findBySenderAccount(String senderAccount);

    List<Payment> findByReceiverAccount(String receiverAccount);

    List<Payment> findByStatus(Payment.PaymentStatus status);
}