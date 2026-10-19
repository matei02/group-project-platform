package groupproject.groupproject_backend;

import groupproject.groupproject_backend.controller.dto.task.TasksOverviewResponse;
import groupproject.groupproject_backend.controller.dto.task.TotalSpendingResponse;
import groupproject.groupproject_backend.domain.Task;
import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.domain.enums.TaskStatus;
import groupproject.groupproject_backend.repository.TaskRepository;
import groupproject.groupproject_backend.repository.UserRepository;
import groupproject.groupproject_backend.repository.converter.TaskConverter;
import groupproject.groupproject_backend.repository.converter.UserConverter;
import groupproject.groupproject_backend.repository.entity.TaskEntity;
import groupproject.groupproject_backend.repository.entity.UserEntity;
import groupproject.groupproject_backend.service.impl.TaskServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TaskServiceImpl taskService;


    @Test
    void getOverviewByCreator_shouldReturnEmptyList_whenNoTasksFoundForCreator() {
        // Arrange
        User creator = new User();
        creator.setId(1L);

        UserEntity mockUserEntity = new UserEntity();
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUserEntity));
        when(taskRepository.findByCreator(any(UserEntity.class))).thenReturn(List.of());

        // Act
        List<Task> overviewList = taskService.getOverviewByCreator(creator.getId());

        // Assert
        assertEquals(0, overviewList.size());
    }


    @Test
    void getOverviewByCreator_shouldReturnListOfTasksOverview_whenTasksFoundForCreator() {
        // Arrange
        User creator = new User();
        creator.setId(1L);

        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUserEntity));

        List<TaskEntity> tasks = Arrays.asList(
                new TaskEntity() {{setId(1); setTitle("Task 1"); setStatus(TaskStatus.ACCEPTED); setCreator(mockUserEntity); }},
                new TaskEntity() {{setId(2); setTitle("Task 2"); setStatus(TaskStatus.PENDING); setCreator(mockUserEntity);}},
                new TaskEntity() {{setId(3); setTitle("Task 3"); setStatus(TaskStatus.DECLINED); setCreator(mockUserEntity);}}
        );
        when(taskRepository.findByCreator(any(UserEntity.class))).thenReturn(tasks);

        List<Task> expectedTasks = tasks.stream()
                .map(TaskConverter::toDomain)
                .collect(Collectors.toList());

        // Act
        List<Task> actualTasks = taskService.getOverviewByCreator(creator.getId());

        // Assert
        assertEquals(expectedTasks.size(), actualTasks.size());
        // Optionally: compare actual titles or statuses too
    }


    @Test
    void getOverviewByCreator_shouldReturnCorrectOverview_withDifferentTaskStatuses() {
        // Arrange
        User creator = new User();
        creator.setId(1L);

        UserEntity mockUserEntity = new UserEntity();
        mockUserEntity.setId(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUserEntity));

        List<TaskEntity> taskEntities = Arrays.asList(
                new TaskEntity() {{ setId(1);setTitle("Accepted Task"); setStatus(TaskStatus.ACCEPTED); setCreator(mockUserEntity);}},
                new TaskEntity() {{setId(2); setTitle("Declined Task"); setStatus(TaskStatus.DECLINED); setCreator(mockUserEntity);}}
        );
        when(taskRepository.findByCreator(any(UserEntity.class))).thenReturn(taskEntities);

        List<Task> expectedTasks = taskEntities.stream()
                .map(TaskConverter::toDomain)
                .collect(Collectors.toList());

        // Act
        List<Task> actualTasks = taskService.getOverviewByCreator(creator.getId());

        // Assert
        assertEquals(expectedTasks.size(), actualTasks.size());
        assertEquals(expectedTasks.get(0).getStatus(), actualTasks.get(0).getStatus());
        assertEquals(expectedTasks.get(1).getTitle(), actualTasks.get(1).getTitle());
    }

    @Test
    public void testArchiveTask_ShouldDeactivateTask_WhenTaskExists() {
        Long taskId = 1L;
        UserEntity user = UserEntity.builder().id(1L).build();

        TaskEntity taskEntity = TaskEntity.builder().id(1).title("Sample task").status(TaskStatus.ACCEPTED).creator(user).build();

        when(taskRepository.findById(taskId)).thenReturn(Optional.of(taskEntity));


        when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

        boolean result = taskService.archiveTask(taskId);

        assertTrue(result);
        verify(taskRepository).findById(taskId);
        verify(taskRepository).save(any(TaskEntity.class));
    }


    @Test
    void getTotalSpendingAndBudgetByCreator_Success() {
        // Arrange
        Long creatorId = 1L;

        UserEntity creatorEntity = new UserEntity();
        creatorEntity.setId(creatorId);

        User creatorDomain = new User();
        creatorDomain.setId(creatorId);

        TaskEntity taskEntity1 = new TaskEntity();
        taskEntity1.setPayout(50.0);
        taskEntity1.setBudget(100.0);
        taskEntity1.setCreator(creatorEntity);

        TaskEntity taskEntity2 = new TaskEntity();
        taskEntity2.setPayout(30.0);
        taskEntity2.setBudget(70.0);
        taskEntity2.setCreator(creatorEntity);

        Task taskDomain1 = new Task();
        taskDomain1.setPayout(50.0);
        taskDomain1.setBudget(100.0);

        Task taskDomain2 = new Task();
        taskDomain2.setPayout(30.0);
        taskDomain2.setBudget(70.0);

        when(userRepository.findById(creatorId)).thenReturn(Optional.of(creatorEntity));

        mockStatic(UserConverter.class);
        when(UserConverter.toDomain(creatorEntity)).thenReturn(creatorDomain);
        when(UserConverter.toEntity(creatorDomain)).thenReturn(creatorEntity);

        when(taskRepository.findByCreator(creatorEntity)).thenReturn(List.of(taskEntity1, taskEntity2));

        mockStatic(TaskConverter.class);
        when(TaskConverter.toDomain(taskEntity1)).thenReturn(taskDomain1);
        when(TaskConverter.toDomain(taskEntity2)).thenReturn(taskDomain2);

        // Act
        TotalSpendingResponse response = taskService.getTotalSpendingAndBudgetByCreator(creatorId);

        // Assert
        assertEquals(80.0, response.getTotalPayout());
        assertEquals(170.0, response.getTotalBudget());
    }

    @Test
    void getTotalSpendingAndBudgetByCreator_NoTasksFound() {
        // Arrange
        Long creatorId = 2L;
        UserEntity creatorEntity = new UserEntity();
        creatorEntity.setId(creatorId);
        User creatorDomain = new User();
        creatorDomain.setId(creatorId);

        when(userRepository.findById(creatorId)).thenReturn(Optional.of(creatorEntity));

        mockStatic(UserConverter.class);
        when(UserConverter.toDomain(creatorEntity)).thenReturn(creatorDomain);
        when(UserConverter.toEntity(creatorDomain)).thenReturn(creatorEntity);

        when(taskRepository.findByCreator(creatorEntity)).thenReturn(List.of());

        // Act
        TotalSpendingResponse response = taskService.getTotalSpendingAndBudgetByCreator(creatorId);

        // Assert
        assertEquals(0.0, response.getTotalPayout());
        assertEquals(0.0, response.getTotalBudget());
    }

    @Test
    void getTotalSpendingAndBudgetByCreator_UserNotFound() {
        // Arrange
        Long creatorId = 3L;
        when(userRepository.findById(creatorId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> taskService.getTotalSpendingAndBudgetByCreator(creatorId));
    }
}