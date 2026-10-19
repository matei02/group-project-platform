package groupproject.groupproject_backend.repository.converter;

import groupproject.groupproject_backend.domain.BugReport;
import groupproject.groupproject_backend.repository.entity.BugReportEntity;

public class BugReportConverter {

    public static BugReportEntity toEntity(BugReport report){
        return BugReportEntity.builder()
                .id(report.getBugReportId())
                .userId(report.getRequestingUserId())
                .taskId(report.getTaskId())
                .status(report.getStatus())
                .reportText(report.getReportText())
                .additionalFiles(report.getAdditionalFiles())
                .screenRecording(report.getScreenRecording())
                .build();
    }

    public static BugReport toDomain(BugReportEntity report){
        return BugReport.builder()
                .bugReportId(report.getId())
                .requestingUserId(report.getUserId())
                .taskId(report.getTaskId())
                .status(report.getStatus())
                .reportText(report.getReportText())
                .additionalFiles(report.getAdditionalFiles())
                .screenRecording(report.getScreenRecording())
                .build();
    }
}
