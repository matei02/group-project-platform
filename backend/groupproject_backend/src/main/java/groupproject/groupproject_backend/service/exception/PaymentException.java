package groupproject.groupproject_backend.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PaymentException extends ResponseStatusException {
    public PaymentException(String reason) {
        super(HttpStatus.PAYMENT_REQUIRED, reason);
    }

}
