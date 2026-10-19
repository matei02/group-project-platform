package groupproject.groupproject_backend.external.impl;

import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StripeServiceImplTest {

    private StripeServiceImpl stripeService;

    @BeforeEach
    void setUp() throws Exception {
        stripeService = new StripeServiceImpl();

        var field = StripeServiceImpl.class.getDeclaredField("apiKeyString");
        field.setAccessible(true);
        field.set(stripeService, "sk_test_12345");

        stripeService.init();
    }

    @Test
    void deposit_shouldReturnClientSecret_whenPaymentIntentIsCreated() throws Exception {
        double amount = 500.0;
        String currency = "eur";

        PaymentIntent mockPaymentIntent = mock(PaymentIntent.class);
        when(mockPaymentIntent.getClientSecret()).thenReturn("pi_abc123_secret_xyz");

        try (MockedStatic<PaymentIntent> mockedStatic = mockStatic(PaymentIntent.class)) {
            mockedStatic.when(() ->
                    PaymentIntent.create(any(PaymentIntentCreateParams.class))
            ).thenReturn(mockPaymentIntent);

            String result = stripeService.deposit(amount, currency);

            assertEquals("pi_abc123_secret_xyz", result);
            mockedStatic.verify(() -> PaymentIntent.create(any(PaymentIntentCreateParams.class)), times(1));
        }
    }
}
