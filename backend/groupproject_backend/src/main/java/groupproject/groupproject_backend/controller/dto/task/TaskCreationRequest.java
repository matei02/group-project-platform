package groupproject.groupproject_backend.controller.dto.task;

import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.domain.enums.DifficultyStatus;
import groupproject.groupproject_backend.domain.enums.TaskStatus;
import lombok.*;

import java.io.File;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskCreationRequest {
    private int id;
    private String title;
    private String description;
    private double payout;
    private String image;
    private double budget;
    private String link;
    private TaskStatus status;
    private DifficultyStatus difficulty;
    private User creator;
}
