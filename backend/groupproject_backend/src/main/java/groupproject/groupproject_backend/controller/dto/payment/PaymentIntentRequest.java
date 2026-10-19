package groupproject.groupproject_backend.controller.dto.payment;


import lombok.Data;

@Data
public class PaymentIntentRequest {
    private long amount;
    private String currency;
}
