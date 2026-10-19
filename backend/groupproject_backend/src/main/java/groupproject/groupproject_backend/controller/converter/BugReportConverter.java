package groupproject.groupproject_backend.controller.converter;


import groupproject.groupproject_backend.controller.dto.report.BugReportRequest;
import groupproject.groupproject_backend.controller.dto.report.BugReportResponse;
import groupproject.groupproject_backend.domain.BugReport;

public class BugReportConverter {
    public static BugReport toDomain(BugReportRequest bugReport){
        return BugReport.builder()
                .bugReportId(bugReport.getBugReportId())
                .reportText(bugReport.getReportText())
                .status(bugReport.getStatus())
                .taskId(bugReport.getTaskId())
                .requestingUserId(bugReport.getRequestingUserId())
                .screenRecording(bugReport.getScreenRecording())
                .additionalFiles(bugReport.getAdditionalFiles())
                .build();
    }


    public static BugReportResponse toResponse(BugReport bugReport){
        return BugReportResponse.builder()
                .bugReportId(bugReport.getBugReportId())
                .reportText(bugReport.getReportText())
                .status(bugReport.getStatus())
                .taskId(bugReport.getTaskId())
                .requestingUserId(bugReport.getRequestingUserId())
                .screenRecording(bugReport.getScreenRecording())
                .additionalFiles(bugReport.getAdditionalFiles())
                .build();
    }
}
