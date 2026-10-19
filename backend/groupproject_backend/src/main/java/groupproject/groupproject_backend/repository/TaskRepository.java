package groupproject.groupproject_backend.repository;

import groupproject.groupproject_backend.domain.Task;
import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.repository.entity.TaskEntity;
import groupproject.groupproject_backend.repository.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<TaskEntity, Long> {
    Optional<TaskEntity> findById(Long id);
    List<TaskEntity> findByCreator(UserEntity creator);
}
