package groupproject.groupproject_backend.external;

public interface StripeService {
    String deposit(double amount, String currency) throws Exception;
}
