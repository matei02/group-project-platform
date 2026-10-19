package groupproject.groupproject_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import groupproject.groupproject_backend.controller.converter.BugReportConverter;
import groupproject.groupproject_backend.controller.dto.report.BugReportRequest;
import groupproject.groupproject_backend.controller.dto.report.BugReportResponse;
import groupproject.groupproject_backend.domain.BugReport;
import groupproject.groupproject_backend.domain.enums.BugReportStatus;
import groupproject.groupproject_backend.service.BugReportService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/report")
@AllArgsConstructor
public class BugReportController {

    private final BugReportService reportService;
    private static final Logger logger = LoggerFactory.getLogger(BugReportController.class);
    private final ObjectMapper objectMapper;

    @PostMapping(value = "/submit", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BugReportResponse> submitBugReport(
            @RequestParam("taskId") Long taskId,
            @RequestParam("userId") Long userId,
            @RequestParam("reportText") String reportText,
            @RequestParam("status") String status,
            @RequestParam("screenRecording") MultipartFile screenRecording,
            @RequestParam(value = "file0", required = false) MultipartFile file0,
            @RequestParam(value = "file1", required = false) MultipartFile file1,
            @RequestParam(value = "file2", required = false) MultipartFile file2,
            @RequestParam(value = "file3", required = false) MultipartFile file3,
            @RequestParam(value = "file4", required = false) MultipartFile file4
    ) {
        List<byte[]> additionalFiles = new ArrayList<>();
        try {
            if (file0 != null) additionalFiles.add(file0.getBytes());
            if (file1 != null) additionalFiles.add(file1.getBytes());
            if (file2 != null) additionalFiles.add(file2.getBytes());
            if (file3 != null) additionalFiles.add(file3.getBytes());
            if (file4 != null) additionalFiles.add(file4.getBytes());
        } catch (Exception e) {
            throw new RuntimeException("Failed to read uploaded files", e);
        }

        byte[] screenRecordingBytes = null;
        try {
            if (screenRecording != null) screenRecordingBytes = screenRecording.getBytes();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read screen recording", e);
        }

        BugReportRequest bugReportRequest = BugReportRequest.builder()
                .taskId(taskId)
                .requestingUserId(userId)
                .reportText(reportText)
                .status(BugReportStatus.PENDING)
                .screenRecording(screenRecordingBytes)
                .additionalFiles(additionalFiles)
                .build();

        BugReport report = reportService.createBugReport(BugReportConverter.toDomain(bugReportRequest));
        logger.info("Created BugReport: {}", report);

        BugReportResponse response = BugReportConverter.toResponse(report);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<BugReportResponse>> getAll() {
        List<BugReport> bugReports = reportService.getAllBugReports();
        List<BugReportResponse> responses = bugReports.stream()
                .map(BugReportConverter::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{bugReportId}/approve")
    public ResponseEntity<Void> approveBugReport(@PathVariable Long bugReportId) {
        try {
            reportService.approveBugReport(bugReportId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            logger.error("Bug report not found", e);
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error approving bug report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{bugReportId}/decline")
    public ResponseEntity<Void> declineBugReport(@PathVariable Long bugReportId) {
        try {
            reportService.declineBugReport(bugReportId);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            logger.error("Bug report not found", e);
            return ResponseEntity.notFound().build();
        }
        catch (Exception e) {
            logger.error("Error approving bug report", e);
            return ResponseEntity.internalServerError().build();
        }
    }

}