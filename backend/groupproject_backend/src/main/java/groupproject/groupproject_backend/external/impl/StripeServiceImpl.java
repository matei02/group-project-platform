package groupproject.groupproject_backend.external.impl;

import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import groupproject.groupproject_backend.external.StripeService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripeServiceImpl implements StripeService {

    @Value("${stripe.apikey}")
    private String apiKeyString;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKeyString;
    }


    public String deposit(double amount, String currency) throws Exception {
            PaymentIntentCreateParams params =
                    PaymentIntentCreateParams.builder()
                                            .setAmount((long) amount)
                                            .setCurrency(currency)
                                            .build();

            return PaymentIntent.create(params).getClientSecret();
    }

}
