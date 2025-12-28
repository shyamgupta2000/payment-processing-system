package com.banking.payment.messaging;

import com.banking.payment.config.JmsConfig;
import com.banking.payment.model.Payment;
import com.banking.payment.repository.PaymentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms. annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class JmsConsumer {

    private final PaymentRepository paymentRepository;
    private final JmsProducer jmsProducer;
    private final ObjectMapper objectMapper;
    private final Random random = new Random();

    @JmsListener(destination = JmsConfig.PAYMENT_QUEUE)
    public void receivePaymentMessage(String message) {
        try {
            log.info("📨 Received payment message from queue");
            Payment payment = objectMapper. readValue(message, Payment.class);

            log.info("🔄 Processing payment: Transaction ID = {}", payment.getTransactionId());

            // Send to processing queue
            jmsProducer.sendProcessingMessage(payment);

        } catch (Exception e) {
            log.error("❌ Error processing payment message: {}", e.getMessage(), e);
        }
    }

    @JmsListener(destination = JmsConfig.PAYMENT_PROCESSING_QUEUE, concurrency = "3-5")
    public void processPayment(String message) {
        try {
            Payment payment = objectMapper.readValue(message, Payment.class);
            log.info("⚙️ Processing payment in worker thread: {}", payment.getTransactionId());

            // Fetch from database
            Payment dbPayment = paymentRepository.findByTransactionId(payment.getTransactionId())
                    .orElse(null);

            if (dbPayment == null) {
                log.warn("⚠️ Payment not found in database:  {}", payment.getTransactionId());
                return;
            }

            // Update status to PROCESSING
            dbPayment.setStatus(Payment.PaymentStatus.PROCESSING);
            paymentRepository.save(dbPayment);

            // Simulate payment processing (2-5 seconds)
            int processingTime = 2000 + random.nextInt(3000);
            Thread.sleep(processingTime);

            // Simulate success/failure (90% success rate)
            boolean isSuccess = random.nextInt(100) < 90;

            if (isSuccess) {
                dbPayment.setStatus(Payment. PaymentStatus. COMPLETED);
                paymentRepository. save(dbPayment);
                log.info("✅ Payment COMPLETED: {} | Amount:  {} {}",
                        dbPayment.getTransactionId(),
                        dbPayment.getAmount(),
                        dbPayment.getCurrency());

                jmsProducer.sendNotificationMessage(
                        dbPayment.getTransactionId(),
                        "COMPLETED",
                        String. format("Payment of %s %s from %s to %s completed successfully",
                                dbPayment.getAmount(),
                                dbPayment.getCurrency(),
                                dbPayment.getSenderAccount(),
                                dbPayment.getReceiverAccount())
                );
            } else {
                dbPayment.setStatus(Payment.PaymentStatus. FAILED);
                paymentRepository. save(dbPayment);
                log.error("❌ Payment FAILED: {}", dbPayment.getTransactionId());

                jmsProducer.sendNotificationMessage(
                        dbPayment. getTransactionId(),
                        "FAILED",
                        "Payment processing failed. Please contact support."
                );
            }

        } catch (Exception e) {
            log.error("❌ Error in payment processing: {}", e.getMessage(), e);
        }
    }

    @JmsListener(destination = JmsConfig.PAYMENT_NOTIFICATION_QUEUE)
    public void receiveNotification(String notification) {
        log.info("🔔 NOTIFICATION: {}", notification);
        // Here you can send emails, SMS, push notifications, etc.
    }
}