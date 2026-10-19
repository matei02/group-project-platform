package groupproject.groupproject_backend.external.impl;

import groupproject.groupproject_backend.external.PaypalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import java.util.Locale;

import java.util.List;
import java.util.Map;
import java.util.Base64;

@Service
public class PaypalServiceImpl implements PaypalService {

    @Value("${paypal.api-base}")
    private String apiBase;

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Autowired
    private RestTemplate restTemplate;


    public String sendPayout(String recipientEmail, double amount, String currency) {
        String accessToken = getPayPalAccessToken();
        String url = apiBase + "/v1/payments/payouts";

        String amountStr = String.format(Locale.US, "%.2f", amount);

        Map<String, Object> requestBody = Map.of(
                "sender_batch_header", Map.of(
                        "email_subject", "You have received a payout!",
                        "email_message", "You have received a payout. Thank you for using our service!"
                ),
                "items", List.of(
                        Map.of(
                                "recipient_type", "EMAIL",
                                "amount", Map.of("currency", currency.toUpperCase(), "value", amountStr),
                                "receiver", recipientEmail,
                                "note", "Payout received!",
                                "sender_item_id", "item-" + System.currentTimeMillis()
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        String responseBody = response.getBody();
        if (responseBody != null && responseBody.contains("payout_batch_id")) {
            String payoutBatchId = responseBody.split("\"payout_batch_id\":\"")[1].split("\"")[0];
            return payoutBatchId;
        } else {
            throw new RuntimeException("Failed to send payout: " + responseBody);
        }
    }

    private String getPayPalAccessToken() {
        String url = apiBase + "/v1/oauth2/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String creds = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        headers.set("Authorization", "Basic " + creds);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.POST, request, JsonNode.class);
        return response.getBody().get("access_token").asText();
    }




}
