package groupproject.groupproject_backend.service.impl;

import groupproject.groupproject_backend.controller.dto.task.TasksOverviewResponse;
import groupproject.groupproject_backend.controller.dto.task.TotalSpendingResponse;
import groupproject.groupproject_backend.repository.UserRepository;
import groupproject.groupproject_backend.repository.converter.TaskConverter;
import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.domain.Task;
import groupproject.groupproject_backend.domain.enums.TaskStatus;
import groupproject.groupproject_backend.repository.TaskRepository;
import groupproject.groupproject_backend.repository.converter.UserConverter;
import groupproject.groupproject_backend.repository.entity.TaskEntity;
import groupproject.groupproject_backend.service.TaskService;
import groupproject.groupproject_backend.service.exception.InsufficientBalanceException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;


    @Transactional
    public int createTask(Task request){
        User taskCreator = UserConverter.toDomain(
                userRepository.findById(request.getCreator().getId()).orElseThrow());



        if (taskCreator.getVirtualMoney() < request.getBudget()){
            throw new InsufficientBalanceException("Not enough virtual money to create task");
        }

        request.setStatus(TaskStatus.PENDING);
        TaskEntity task = TaskConverter.toEntity(request);
        System.out.println("Task: " + task);
        Task savedTask = TaskConverter.toDomain(taskRepository.save(task));
        return savedTask.getId();
    }

    public List<Task> getOverviewByCreator(Long creatorId) {
        User user = UserConverter.toDomain(userRepository.findById(creatorId).orElseThrow());
        return taskRepository.findByCreator(UserConverter.toEntity(user)).stream()
                .map(TaskConverter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public TotalSpendingResponse getTotalSpendingAndBudgetByCreator(Long creatorId) {
        User user = UserConverter.toDomain(userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found")));

        List<Task> tasks = taskRepository.findByCreator(UserConverter.toEntity(user)).stream()
                .map(TaskConverter::toDomain)
                .collect(Collectors.toList());

        double totalPayout = tasks.stream()
                .mapToDouble(Task::getPayout)
                .sum();

        double totalBudget = tasks.stream()
                .mapToDouble(Task::getBudget)
                .sum();

        return new TotalSpendingResponse(totalPayout, totalBudget);
    }



    @Transactional
    public Task updateTask(Long taskId, Task task) {
        Task existingTask = TaskConverter.toDomain(taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId)));

        existingTask.setDescription(task.getDescription());
        existingTask.setBudget(task.getBudget());
        existingTask.setTitle(task.getTitle());
        existingTask.setLink(task.getLink());
        existingTask.setPayout(task.getPayout());
        existingTask.setStatus(task.getStatus());
        existingTask.setDifficulty(task.getDifficulty());


        return TaskConverter.toDomain(taskRepository.save(TaskConverter.toEntity(existingTask)));
    }

    @Transactional
    public boolean archiveTask(@PathVariable Long taskId) {
        Task existingTask = TaskConverter.toDomain(taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId)));

        existingTask.setStatus(TaskStatus.DEACTIVATED);

        return Objects.nonNull(TaskConverter.toDomain(taskRepository.save(TaskConverter.toEntity(existingTask))));
    }


    @Transactional
    public void approveTask(Long taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        task.setStatus(TaskStatus.ACCEPTED);
        taskRepository.save(task);
    }

    @Transactional
    public void declineTask(Long taskId) {
        TaskEntity task = taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with ID: " + taskId));

        task.setStatus(TaskStatus.DECLINED);
        taskRepository.save(task);
    }

    @Transactional
    public List<Task> getAllTasks() {
        List<TaskEntity> tasks = taskRepository.findAll();
        return tasks.stream()
                .map(TaskConverter::toDomain)
                .collect(Collectors.toList());
    }


}
