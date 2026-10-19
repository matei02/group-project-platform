package groupproject.groupproject_backend.domain;


import groupproject.groupproject_backend.domain.enums.PaymentType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    private Long id;
//    private User user;
    private PaymentType paymentType;
    private double amount;
    private String currency;
    private LocalDateTime date;
}
