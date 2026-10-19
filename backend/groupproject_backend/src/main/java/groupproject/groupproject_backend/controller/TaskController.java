package groupproject.groupproject_backend.controller;

import groupproject.groupproject_backend.controller.converter.TaskConverter;
import groupproject.groupproject_backend.controller.dto.task.TaskCreationRequest;
import groupproject.groupproject_backend.controller.dto.task.TaskCreationResponse;
import groupproject.groupproject_backend.controller.dto.task.TasksOverviewResponse;
import groupproject.groupproject_backend.controller.dto.task.TotalSpendingResponse;
import groupproject.groupproject_backend.domain.Task;
import groupproject.groupproject_backend.service.TaskService;
import groupproject.groupproject_backend.service.exception.InsufficientBalanceException;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;


@RestController
@RequestMapping("/tasks")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskCreationResponse> createTask( @RequestBody TaskCreationRequest taskCreationRequest){
        try{
            System.out.println("TaskCreation: " + taskCreationRequest);
            int response_id = taskService.createTask(TaskConverter.toDomain(taskCreationRequest));
            TaskCreationResponse response = TaskCreationResponse.builder()
                    .id(response_id)
                    .build();
            return ResponseEntity.ok().body(response);
        } catch (InsufficientBalanceException e){
            return ResponseEntity.badRequest()
                    .header("Error", "You don't have enough virtual money to create this task.")
                    .header("Access-Control-Expose-Headers", "Error")
                    .body(null);
        }
        catch (Exception e){
            return ResponseEntity.internalServerError().build();
        }
    }


    @PutMapping("/{taskId}")
    public ResponseEntity<TaskCreationResponse> updateTask(@PathVariable Long taskId, @RequestBody TaskCreationRequest taskDTO) {
        try {
            Task updatedTask = taskService.updateTask(taskId, TaskConverter.toDomain(taskDTO));
            return ResponseEntity.ok().body(TaskConverter.toDTO(updatedTask));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> archiveTask(@PathVariable Long taskId) {
        try{
            taskService.archiveTask(taskId);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }


    @GetMapping("/creator/{creatorId}/overview")
    public ResponseEntity<TasksOverviewResponse> getOverviewByCreator(@PathVariable long creatorId) {
       try{
           TasksOverviewResponse overview = TasksOverviewResponse.builder().tasks(taskService.getOverviewByCreator(creatorId)).build();
           return ResponseEntity.ok(overview);
       } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
       }
    }

    @GetMapping("/creator/{creatorId}/total-spending")
    public ResponseEntity<TotalSpendingResponse> getTotalSpendingAndBudgetByCreator(@PathVariable long creatorId) {
        try {
            TotalSpendingResponse totalSpendingResponse = taskService.getTotalSpendingAndBudgetByCreator(creatorId);
            return ResponseEntity.ok(totalSpendingResponse);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{taskId}/approve")
    public ResponseEntity<Void> approveTask(@PathVariable Long taskId) {
        try {
            taskService.approveTask(taskId); // Service should return void or Task
            return ResponseEntity.ok().build(); // Just return 200 OK with no body
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{taskId}/decline")
    public ResponseEntity<Void> declineTask(@PathVariable Long taskId) {
        try {
            taskService.declineTask(taskId); // Service should return void or Task
            return ResponseEntity.ok().build(); // Just return 200 OK with no body
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping
    public ResponseEntity<TasksOverviewResponse> getAllTasks() {
        try {
            TasksOverviewResponse responseTasks = TasksOverviewResponse.builder().tasks(taskService.getAllTasks()).build();
            return ResponseEntity.ok().body(responseTasks);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
