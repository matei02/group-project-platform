package groupproject.groupproject_backend.repository.converter;

import groupproject.groupproject_backend.domain.Payment;
import groupproject.groupproject_backend.repository.entity.PaymentEntity;

public class PaymentConverter {

    public static PaymentEntity toEntity(Payment payment){
        return PaymentEntity.builder()
                .id(payment.getId())
                .paymentType(payment.getPaymentType())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .date(payment.getDate())
                .build();
    }

    public static Payment toDomain(PaymentEntity payment){
        return Payment.builder()
                .id(payment.getId())
                .paymentType(payment.getPaymentType())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .date(payment.getDate())
                .build();
    }
}
