package groupproject.groupproject_backend.controller.converter;

import groupproject.groupproject_backend.controller.dto.task.TaskCreationRequest;
import groupproject.groupproject_backend.controller.dto.task.TaskCreationResponse;
import groupproject.groupproject_backend.domain.Task;

import java.util.Base64;

public class TaskConverter {
    public static TaskCreationResponse toDTO(Task task){
        return TaskCreationResponse.builder()
                .id(task.getId())
                .build();
    }

  public static Task toDomain(TaskCreationRequest request){
//      byte[] fileBytes = new byte[0];
//
//      if (request.getImage() != null) {
//          fileBytes = Base64.getDecoder().decode(request.getImage());
//
//      }
        return Task.builder()
                .id(request.getId())
                .title(request.getTitle())
                .description(request.getDescription())
                .payout(request.getPayout())
                .link(request.getLink())
                .image(request.getImage())
                .difficulty(request.getDifficulty())
                .budget(request.getBudget())
                .creator(request.getCreator())
                .status(request.getStatus())
                .build();
  }
}
