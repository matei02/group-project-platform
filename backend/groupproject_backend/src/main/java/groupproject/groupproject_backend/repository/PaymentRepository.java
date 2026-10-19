package groupproject.groupproject_backend.repository;

import groupproject.groupproject_backend.repository.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<PaymentEntity, Long> {
}
