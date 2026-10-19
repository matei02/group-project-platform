package groupproject.groupproject_backend.service;

import groupproject.groupproject_backend.domain.BugReport;

import java.util.List;

public interface BugReportService {
    BugReport createBugReport(BugReport report);
    List<BugReport> getAllBugReports();
    Void approveBugReport(Long reportId);
    Void declineBugReport(Long reportId);
}
