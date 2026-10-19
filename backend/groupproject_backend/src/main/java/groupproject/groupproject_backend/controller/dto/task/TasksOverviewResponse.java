package groupproject.groupproject_backend.controller.dto.task;

import groupproject.groupproject_backend.domain.Task;
import groupproject.groupproject_backend.domain.enums.DifficultyStatus;
import groupproject.groupproject_backend.domain.enums.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class TasksOverviewResponse {
   List<Task> tasks;
   private Double totalSpendings;
}
