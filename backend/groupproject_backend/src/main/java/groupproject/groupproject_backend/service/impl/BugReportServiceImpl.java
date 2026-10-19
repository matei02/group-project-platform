package groupproject.groupproject_backend.service.impl;

import groupproject.groupproject_backend.repository.converter.BugReportConverter;
import groupproject.groupproject_backend.domain.BugReport;
import groupproject.groupproject_backend.domain.enums.BugReportStatus;
import groupproject.groupproject_backend.repository.BugReportRepository;
import groupproject.groupproject_backend.repository.entity.BugReportEntity;
import groupproject.groupproject_backend.service.BugReportService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BugReportServiceImpl implements BugReportService {
    private final BugReportRepository reportRepository;

    public BugReportServiceImpl(BugReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    @Override
    public BugReport createBugReport(BugReport report) {
        report.setStatus(BugReportStatus.PENDING);
        BugReportEntity reportToSave = BugReportConverter.toEntity(report);
        return BugReportConverter.toDomain(reportRepository.save(reportToSave));
    }

    @Override
    public List<BugReport> getAllBugReports() {
        return reportRepository.findAll().stream()
                .map(BugReportConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Void approveBugReport(Long reportId) {
        Optional<BugReportEntity> report = reportRepository.findById(reportId);
        if (report.isPresent()) {
            BugReportEntity reportEntity = report.get();
            reportEntity.setStatus(BugReportStatus.APPROVED);
            reportRepository.save(reportEntity);
            return null;
        } else {
            throw new EntityNotFoundException("Bug report not found with id " + reportId);
        }
    }

    @Override
    public Void declineBugReport(Long reportId) {
        Optional<BugReportEntity> report = reportRepository.findById(reportId);
        if (report.isPresent()) {
            BugReportEntity reportEntity = report.get();
            reportEntity.setStatus(BugReportStatus.DECLINED);
            reportRepository.save(reportEntity);
            return null;
        } else {
            throw new EntityNotFoundException("Bug report not found with id " + reportId);
        }
    }
}
