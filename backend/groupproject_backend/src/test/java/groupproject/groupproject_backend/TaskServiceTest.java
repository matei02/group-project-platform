//package groupproject.groupproject_backend;
//
//import groupproject.groupproject_backend.controller.dto.task.TaskCreationRequest;
//import groupproject.groupproject_backend.domain.Task;
//import groupproject.groupproject_backend.domain.enums.TaskStatus;
//import groupproject.groupproject_backend.dto.task.TaskDTO;
//import groupproject.groupproject_backend.repository.TaskRepository;
//import groupproject.groupproject_backend.service.impl.TaskServiceImpl;
//import jakarta.transaction.Transactional;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.rest.webmvc.ResourceNotFoundException;
//
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class TaskServiceTest {
//
//    @Mock
//    private TaskRepository taskRepository;
//
//    @InjectMocks
//    private TaskServiceImpl taskService;
//
//    private Task task;
//    private TaskCreationRequest taskDTO;
//
//    @BeforeEach
//    void setUp() {
//        taskDTO = TaskCreationRequest.builder()
//                .id(1)
//                .title("Test Task")
//                .description("Task Description")
//                .payout(20.0)
//                .budget(20.0)
//                .link("task.link.nl")
//                .status(TaskStatus.PENDING)
//                .build();
//
//        task = Task.builder()
//                .id(1)
//                .title("Test Task")
//                .description("Task Description")
//                .payout(20.0)
//                .budget(20.0)
//                .link("task.link.nl")
//                .status(TaskStatus.PENDING)
//                .build();
//    }
//
//    @Test
//    @Transactional
//    void createTask_ShouldSaveTaskAndReturnDTO() {
//        when(taskRepository.save(any(Task.class))).thenReturn(task);
//
//        int result = taskService.createTask(taskDTO);
//
//        assertNotNull(result);
//        assertEquals(task.getTitle(), result.getTitle());
//        assertEquals(task.getDescription(), result.getDescription());
//        assertEquals(task.getPayout(), result.getPayout());
//        assertEquals(task.getBudget(), result.getBudget());
//        assertEquals(task.getLink(), result.getLink());
//        assertEquals(task.getStatus(), result.getStatus());
//
//        verify(taskRepository, times(1)).save(any(Task.class));
//    }
//
//    @Test
//    void updateTask_Success() {
//        Long taskId = 1L; // Use Long instead of int
//
//        TaskCreationRequest taskDTO = new TaskCreationRequest(1, "Updated Title", "Updated Description", 20.00, 20.00, "task.com", TaskStatus.DEACTIVATED);
//
//        Task existingTask = new Task(1, "Title", "Description", 10.00, 10.00, "task.co.nl", TaskStatus.PENDING);
//        Task updatedTask = new Task(1, "Updated Title", "Updated Description", 20.00, 20.00, "task.com", TaskStatus.DEACTIVATED);
//
//        when(taskRepository.findById(taskId)).thenReturn(Optional.of(existingTask));
//        when(taskRepository.save(any(Task.class))).thenReturn(updatedTask);
//
//        TaskCreationRequest result = taskService.updateTask(taskId, taskDTO);
//
//        assertNotNull(result);
//        assertEquals(taskDTO.getTitle(), result.getTitle());
//        assertEquals(taskDTO.getDescription(), result.getDescription());
//        assertEquals(taskDTO.getStatus(), result.getStatus());
//
//        verify(taskRepository, times(1)).findById(taskId);
//        verify(taskRepository, times(1)).save(any(Task.class));
//    }
//
//    @Test
//    void updateTask_TaskNotFound() {
//        Long taskId = 2L;
//        TaskCreationRequest taskDTO = new TaskCreationRequest(1, "Updated Title", "Updated Description", 20.00, 20.00, "task.com", TaskStatus.DEACTIVATED);
//
//        when(taskRepository.findById(taskId)).thenReturn(Optional.empty());
//
//        assertThrows(ResourceNotFoundException.class, () -> taskService.updateTask(taskId, taskDTO));
//
//        verify(taskRepository, times(1)).findById(taskId);
//        verify(taskRepository, never()).save(any(Task.class));
//    }
//}
