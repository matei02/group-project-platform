package groupproject.groupproject_backend.repository.entity;

import groupproject.groupproject_backend.domain.enums.BugReportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class BugReportEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long taskId;

    @Enumerated(EnumType.STRING)
    private BugReportStatus status;

    @Column(length = 2000)
    private String reportText;

    @Lob
    private byte[] screenRecording;

    @ElementCollection
    @CollectionTable(name = "bug_report_additional_files", joinColumns = @JoinColumn(name = "bug_report_id"))
    @Column(name = "file_data", columnDefinition = "BLOB") // Explicitly set column type to BLOB
    private List<byte[]> additionalFiles;
}
