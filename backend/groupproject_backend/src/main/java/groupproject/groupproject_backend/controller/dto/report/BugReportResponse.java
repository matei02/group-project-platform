package groupproject.groupproject_backend.controller.dto.report;


import groupproject.groupproject_backend.domain.enums.BugReportStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BugReportResponse {
    private Long bugReportId;
    private Long taskId;
    private Long requestingUserId;
    private String reportText;
    private BugReportStatus status;
    private byte[] screenRecording;
    private List<byte[]> additionalFiles;
}
