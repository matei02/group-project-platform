package groupproject.groupproject_backend.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "\"USER\"")
@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "virtual_money")
    private double virtualMoney;
}
