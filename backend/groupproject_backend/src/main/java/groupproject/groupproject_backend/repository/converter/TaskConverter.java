package groupproject.groupproject_backend.repository.converter;

import groupproject.groupproject_backend.domain.Task;
import groupproject.groupproject_backend.repository.entity.TaskEntity;

import java.util.Base64;

public class TaskConverter {

    public static TaskEntity toEntity(Task task) {
        byte[] fileBytes = new byte[0];

      if (task.getImage() != null) {
          fileBytes = Base64.getDecoder().decode(task.getImage());

      }
        return TaskEntity.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .payout(task.getPayout())
                .budget(task.getBudget())
                .image(fileBytes)
                .link(task.getLink())
                .difficultyStatus(task.getDifficulty())
                .status(task.getStatus())
                .creator(UserConverter.toEntity(task.getCreator()))
                .build();
    }

    public static Task toDomain(TaskEntity task) {
        String base64Image = "";

        if (task.getImage() != null && task.getImage().length > 0) {
            base64Image = Base64.getEncoder().encodeToString(task.getImage());
        }


        return Task.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .payout(task.getPayout())
                .budget(task.getBudget())
                .image(base64Image)
                .link(task.getLink())
                .difficulty(task.getDifficultyStatus())
                .status(task.getStatus())
                .creator(UserConverter.toDomain(task.getCreator()))
                .build();
    }
}
