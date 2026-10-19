package groupproject.groupproject_backend.external;

public interface PaypalService {
    String sendPayout(String recipientEmail, double amount, String currency);
}
