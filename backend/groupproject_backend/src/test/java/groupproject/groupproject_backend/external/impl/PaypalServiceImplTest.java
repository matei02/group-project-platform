package groupproject.groupproject_backend.external.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaypalServiceImplTest {

    @Mock
    private RestTemplate restTemplateMock;

    @InjectMocks
    private PaypalServiceImpl paypalService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        setField("apiBase", "https://api.sandbox.paypal.com");
        setField("clientId", "change-me-to-a-long-random-secret-of-at-least-32-bytes");
        setField("clientSecret", "change-me-to-a-long-random-secret-of-at-least-32-bytes");
    }

    private void setField(String fieldName, Object value) throws Exception {
        var field = PaypalServiceImpl.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(paypalService, value);
    }

    @Test
    void sendPayout_shouldReturnPayoutBatchId_whenSuccessful() throws Exception {
        JsonNode mockAccessTokenResponse = objectMapper.readTree("{\"access_token\": \"fake-token\"}");
        ResponseEntity<JsonNode> tokenResponse = new ResponseEntity<>(mockAccessTokenResponse, HttpStatus.OK);

        when(restTemplateMock.exchange(
                eq("https://api.sandbox.paypal.com/v1/oauth2/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(JsonNode.class))
        ).thenReturn(tokenResponse);

        String expectedBatchId = "batch-123456";
        String payoutResponseJson = "{\"payout_batch_id\":\"" + expectedBatchId + "\"}";
        ResponseEntity<String> payoutResponse = new ResponseEntity<>(payoutResponseJson, HttpStatus.OK);

        when(restTemplateMock.exchange(
                eq("https://api.sandbox.paypal.com/v1/payments/payouts"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class))
        ).thenReturn(payoutResponse);

        String result = paypalService.sendPayout("recipient@example.com", 42.0, "eur");

        assertEquals(expectedBatchId, result, "The payout batch ID should match expected value");

        verify(restTemplateMock, times(1)).exchange(
                eq("https://api.sandbox.paypal.com/v1/oauth2/token"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(JsonNode.class)
        );

        verify(restTemplateMock, times(1)).exchange(
                eq("https://api.sandbox.paypal.com/v1/payments/payouts"),
                eq(HttpMethod.POST),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    @Test
    void sendPayout_shouldThrowException_whenResponseDoesNotContainPayoutBatchId() throws Exception {
        JsonNode mockAccessTokenResponse = objectMapper.readTree("{\"access_token\": \"fake-token\"}");
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.POST), any(), eq(JsonNode.class)))
                .thenReturn(new ResponseEntity<>(mockAccessTokenResponse, HttpStatus.OK));

        String responseBody = "{\"error\":\"no batch id\"}";
        when(restTemplateMock.exchange(anyString(), eq(HttpMethod.POST), any(), eq(String.class)))
                .thenReturn(new ResponseEntity<>(responseBody, HttpStatus.OK));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> paypalService.sendPayout("recipient@example.com", 100.0, "eur"));

        assertTrue(exception.getMessage().contains("Failed to send payout"), "Should throw exception with error message");
    }
}
