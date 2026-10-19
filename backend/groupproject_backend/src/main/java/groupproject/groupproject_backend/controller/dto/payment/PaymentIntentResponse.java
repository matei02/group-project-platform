package groupproject.groupproject_backend.controller.dto.payment;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentIntentResponse {
    private long amount;
    private String currency;
    private String clientSecret;
}
