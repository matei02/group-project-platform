package groupproject.groupproject_backend.domain;

import groupproject.groupproject_backend.domain.enums.BugReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BugReport {
    private Long bugReportId;
    private Long taskId;
    private Long requestingUserId;
    private String reportText;
    private BugReportStatus status;
    private byte[] screenRecording;
    private List<byte[]> additionalFiles;
}

