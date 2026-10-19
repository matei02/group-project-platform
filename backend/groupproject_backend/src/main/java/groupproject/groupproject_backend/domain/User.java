package groupproject.groupproject_backend.domain;

import jakarta.persistence.*;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Long id;
    private String username;
    private double virtualMoney;
}
