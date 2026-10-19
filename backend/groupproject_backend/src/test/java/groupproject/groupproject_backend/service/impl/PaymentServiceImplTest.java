package groupproject.groupproject_backend.service.impl;

import groupproject.groupproject_backend.domain.Payment;
import groupproject.groupproject_backend.external.PaypalService;
import groupproject.groupproject_backend.external.StripeService;
import groupproject.groupproject_backend.repository.PaymentRepository;
import groupproject.groupproject_backend.repository.entity.PaymentEntity;
import groupproject.groupproject_backend.service.exception.PaymentException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private StripeService stripeServiceMock;

    @Mock
    private PaypalService paypalServiceMock;

    @Mock
    private PaymentRepository paymentRepositoryMock;

    @InjectMocks
    private PaymentServiceImpl paymentService;


    @Test
    void deposit_shouldReturnTransactionId_whenInputIsValid() throws Exception {
        when(stripeServiceMock.deposit(100.0, "GBP")).thenReturn("txn_123");
        when(paymentRepositoryMock.save(any(PaymentEntity.class))).thenReturn(new PaymentEntity());

        String result = paymentService.deposit(100.0, "GBP");

        assertEquals("txn_123", result);
        verify(stripeServiceMock, times(1)).deposit(100.0, "GBP");
    }

    @Test
    void deposit_shouldThrowException_whenAmountIsZero() {
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.deposit(0.0, "GBP"));
    }

    @Test
    void deposit_shouldThrowException_whenCurrencyIsNull() {
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.deposit(50.0, null));
    }

    @Test
    void deposit_shouldThrowException_whenCurrencyIsEmpty() {
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.deposit(50.0, ""));
    }

    @Test
    void withdraw_shouldReturnTransactionId_whenInputIsValid() {
        when(paypalServiceMock.sendPayout("user@example.com", 100.0, "eur")).thenReturn("payout_456");
        when(paymentRepositoryMock.save(any(PaymentEntity.class))).thenReturn(new PaymentEntity());

        String result = paymentService.withdraw("user@example.com", 100.0, "eur");

        assertEquals("payout_456", result);
        verify(paypalServiceMock, times(1)).sendPayout("user@example.com", 100.0, "eur");
    }

    @Test
    void withdraw_shouldThrowException_whenAmountIsNegative() {
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.withdraw("user@example.com", -10.0, "eur"));
    }

    @Test
    void withdraw_shouldThrowException_whenEmailIsNull() {
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.withdraw(null, 50.0, "eur"));
    }

    @Test
    void withdraw_shouldThrowException_whenEmailIsEmpty() {
        PaymentException exception = assertThrows(PaymentException.class,
                () -> paymentService.withdraw("", 50.0, "eur"));
    }
}
