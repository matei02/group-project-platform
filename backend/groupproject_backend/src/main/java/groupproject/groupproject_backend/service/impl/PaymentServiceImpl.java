package groupproject.groupproject_backend.service.impl;

import groupproject.groupproject_backend.domain.Payment;
import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.domain.enums.PaymentType;
import groupproject.groupproject_backend.external.PaypalService;
import groupproject.groupproject_backend.external.StripeService;
import groupproject.groupproject_backend.repository.PaymentRepository;
import groupproject.groupproject_backend.repository.UserRepository;
import groupproject.groupproject_backend.repository.converter.PaymentConverter;
import groupproject.groupproject_backend.repository.converter.UserConverter;
import groupproject.groupproject_backend.service.PaymentService;
import groupproject.groupproject_backend.service.exception.InsufficientBalanceException;
import groupproject.groupproject_backend.service.exception.PaymentException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private StripeService stripeService;
    private PaypalService paypalService;
    private PaymentRepository paymentRepository;
    private UserRepository userRepository;

    public String deposit(double amount, String currency) throws Exception {
        if (amount <= 0 ) {
            throw new PaymentException("Amount must be greater than 0");
        }

        if (currency == null || currency.isEmpty()) {
            throw new PaymentException("Currency must be set");
        }

        Payment payment = Payment.builder()
                .amount(amount)
                .currency(currency)
                .date(LocalDateTime.now())
                .paymentType(PaymentType.DEPOSIT)
                .build();

        paymentRepository.save(PaymentConverter.toEntity(payment));
        return stripeService.deposit(amount, currency);
    }

    public String withdraw(String recipientEmail, double amount, String currency, Long userId){
        if (amount <= 0 ) {
            throw new PaymentException("Amount must be greater than 0");
        }

        User user = UserConverter.toDomain(
                userRepository.findById(userId).orElseThrow());

        if (user.getVirtualMoney() < amount){
            throw new InsufficientBalanceException("Not enough virtual money to withdraw");
        }

        if(recipientEmail == null || recipientEmail.isEmpty()){
            throw new PaymentException("Recipient email must be filled in");
        }


        Payment payment = Payment.builder()
                .amount(amount)
//                .currency(currency)
                .date(LocalDateTime.now())
                .paymentType(PaymentType.WITHDRAW)
                .build();

        paymentRepository.save(PaymentConverter.toEntity(payment));
        return paypalService.sendPayout(recipientEmail, amount, currency);
    }
}
