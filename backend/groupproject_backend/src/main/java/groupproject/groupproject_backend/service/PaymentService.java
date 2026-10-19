package groupproject.groupproject_backend.service;

public interface PaymentService {
    String deposit(double amount, String currency) throws Exception;
    String withdraw(String recipientEmail, double amount, String currency, Long userId);
}
