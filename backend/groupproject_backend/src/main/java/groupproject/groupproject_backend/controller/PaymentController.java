package groupproject.groupproject_backend.controller;

import groupproject.groupproject_backend.controller.dto.payment.PaymentIntentRequest;
import groupproject.groupproject_backend.controller.dto.payment.PaymentIntentResponse;
import groupproject.groupproject_backend.service.PaymentService;
import groupproject.groupproject_backend.service.exception.InsufficientBalanceException;
import groupproject.groupproject_backend.service.exception.PaymentException;
import groupproject.groupproject_backend.controller.dto.payment.PayoutRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payment")
@AllArgsConstructor
@Slf4j
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/deposit")
    public ResponseEntity<PaymentIntentResponse> createPaymentIntent(@RequestBody PaymentIntentRequest paymentRequest) throws Exception {
        try{
            double amountInCents = paymentRequest.getAmount() * 100;
            String clientSecret = paymentService.deposit(amountInCents, paymentRequest.getCurrency());

            PaymentIntentResponse paymentIntentResponse = PaymentIntentResponse.builder()
                    .currency(paymentRequest.getCurrency())
                    .amount(paymentRequest.getAmount())
                    .clientSecret(clientSecret).build();
            return ResponseEntity.ok(paymentIntentResponse);

        }
        catch(PaymentException ex){
            return ResponseEntity.badRequest().build();
        }
        catch(Exception e){
            throw new Exception(e.getMessage());
        }

    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> sendPayout(@RequestBody PayoutRequest request) {
        try {
            String payoutBatchId = paymentService.withdraw(request.getRecipientEmail(), request.getAmount(),
                    request.getCurrency(), request.getUserId());
            return ResponseEntity.ok("Payout sent successfully. Batch ID: " + payoutBatchId);
        } catch (PaymentException e) {
            log.error("Payment exception during withdrawal: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("There was a problem with the payout. Please try again later.");
        } catch (InsufficientBalanceException e) {
            log.warn("User has insufficient balance: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body("You don't have enough money to withdraw.");
        } catch (Exception e) {
            log.error("Unexpected error during payout: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Failed to send payout");
        }
    }

}
