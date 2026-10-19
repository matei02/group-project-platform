package groupproject.groupproject_backend.service;

import groupproject.groupproject_backend.controller.dto.task.TotalSpendingResponse;
import groupproject.groupproject_backend.domain.Task;
import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.controller.dto.task.TasksOverviewResponse;

import java.util.List;
import groupproject.groupproject_backend.controller.dto.task.TaskCreationRequest;
import groupproject.groupproject_backend.repository.entity.TaskEntity;
import org.springframework.web.bind.annotation.PathVariable;

public interface TaskService {
    //TaskDTO createTask(TaskDTO taskDTO);
    Task updateTask(Long taskId, Task task);
    List<Task> getOverviewByCreator(Long creator);
    int createTask(Task taskCreationRequest);
    void approveTask(Long taskId);
    void declineTask(Long taskId);
    List<Task> getAllTasks() ;
//    Double getTotalSpendingsByCreator(Long creatorId);
    TotalSpendingResponse getTotalSpendingAndBudgetByCreator(Long creatorId);
    boolean archiveTask(@PathVariable Long taskId);
}
