package com.banking.payment.messaging;

import com.banking.payment.config.JmsConfig;
import com.banking.payment.model.Payment;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson. databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JmsProducer {

    private final JmsTemplate jmsTemplate;
    private final ObjectMapper objectMapper;

    public void sendPaymentMessage(Payment payment) {
        try {
            String message = objectMapper.writeValueAsString(payment);
            jmsTemplate.convertAndSend(JmsConfig.PAYMENT_QUEUE, message);
            log.info("✅ Sent payment message to queue: Transaction ID = {}", payment.getTransactionId());
        } catch (JsonProcessingException e) {
            log.error("❌ Error converting payment to JSON:  {}", e.getMessage());
        }
    }

    public void sendProcessingMessage(Payment payment) {
        try {
            String message = objectMapper.writeValueAsString(payment);
            jmsTemplate.convertAndSend(JmsConfig.PAYMENT_PROCESSING_QUEUE, message);
            log.info("✅ Sent payment to processing queue: Transaction ID = {}", payment.getTransactionId());
        } catch (JsonProcessingException e) {
            log.error("❌ Error sending to processing queue: {}", e.getMessage());
        }
    }

    public void sendNotificationMessage(String transactionId, String status, String message) {
        String notification = String.format(
                "{\"transactionId\":\"%s\", \"status\":\"%s\", \"message\":\"%s\", \"timestamp\":\"%s\"}",
                transactionId, status, message, java.time.LocalDateTime.now()
        );
        jmsTemplate.convertAndSend(JmsConfig.PAYMENT_NOTIFICATION_QUEUE, notification);
        log.info("✅ Sent notification:  {} - {}", transactionId, status);
    }
}