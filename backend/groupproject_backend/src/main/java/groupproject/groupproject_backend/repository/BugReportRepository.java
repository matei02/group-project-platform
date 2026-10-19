package groupproject.groupproject_backend.repository;

import groupproject.groupproject_backend.domain.BugReport;
import groupproject.groupproject_backend.repository.entity.BugReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BugReportRepository extends JpaRepository<BugReportEntity, Long> {
}
