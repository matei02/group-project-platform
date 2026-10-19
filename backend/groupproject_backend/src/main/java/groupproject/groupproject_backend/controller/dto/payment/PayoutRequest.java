package groupproject.groupproject_backend.controller.dto.payment;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PayoutRequest {
    private String recipientEmail;
    private double amount;
    private String currency;
    private Long userId;
}