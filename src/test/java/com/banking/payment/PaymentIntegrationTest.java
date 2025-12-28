package com.banking.payment;

import com.banking.payment.dto.PaymentRequest;
import com.banking.payment.dto.PaymentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context. TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api. Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest. WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext(classMode = DirtiesContext. ClassMode.AFTER_EACH_TEST_METHOD)
class PaymentIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testCreateAndRetrievePayment() {
        // Create payment request
        PaymentRequest request = PaymentRequest.builder()
                .senderAccount("1234567890")
                .receiverAccount("0987654321")
                .amount(new BigDecimal("1000.00"))
                .currency("INR")
                .paymentMethod("UPI")
                .description("Integration test payment")
                .build();

        // Create payment
        ResponseEntity<PaymentResponse> createResponse = restTemplate.postForEntity(
                "/api/payments",
                request,
                PaymentResponse.class
        );

        // Assert creation
        assertEquals(HttpStatus.CREATED, createResponse.getStatusCode());
        assertNotNull(createResponse.getBody());
        assertNotNull(createResponse.getBody().getTransactionId());
        assertEquals("1234567890", createResponse.getBody().getSenderAccount());

        // Retrieve payment
        Long paymentId = createResponse.getBody().getId();
        ResponseEntity<PaymentResponse> getResponse = restTemplate.getForEntity(
                "/api/payments/" + paymentId,
                PaymentResponse.class
        );

        // Assert retrieval
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertNotNull(getResponse.getBody());
        assertEquals("1234567890", getResponse. getBody().getSenderAccount());
        assertEquals(new BigDecimal("1000.00"), getResponse.getBody().getAmount());
    }

    @Test
    void testGetAllPayments() {
        // Create a payment first
        PaymentRequest request = PaymentRequest.builder()
                .senderAccount("9876543210")
                .receiverAccount("1234567890")
                .amount(new BigDecimal("500.00"))
                .currency("USD")
                .paymentMethod("Credit Card")
                .build();

        restTemplate.postForEntity("/api/payments", request, PaymentResponse. class);

        // Get all payments
        ResponseEntity<PaymentResponse[]> response = restTemplate.getForEntity(
                "/api/payments",
                PaymentResponse[].class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().length > 0);
    }

    @Test
    void testInvalidPaymentRequest() {
        // Invalid request (amount too low)
        PaymentRequest invalidRequest = PaymentRequest.builder()
                .senderAccount("123")  // Too short
                .receiverAccount("0987654321")
                .amount(new BigDecimal("0.00"))  // Invalid amount
                .currency("INR")
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/payments",
                invalidRequest,
                String.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}