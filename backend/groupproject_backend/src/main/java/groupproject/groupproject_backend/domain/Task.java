package groupproject.groupproject_backend.domain;

import groupproject.groupproject_backend.domain.enums.DifficultyStatus;
import groupproject.groupproject_backend.domain.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;

import java.io.File;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    private int id;
    private String title;
    private String description;
    private String image;
    //private User creators;
    private double payout;
    private double budget;
    private String link;
    private DifficultyStatus difficulty;
    private TaskStatus status;
    private User creator;

}
