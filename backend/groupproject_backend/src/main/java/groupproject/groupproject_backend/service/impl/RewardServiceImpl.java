package groupproject.groupproject_backend.service.impl;

import groupproject.groupproject_backend.domain.Task;
import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.repository.TaskRepository;
import groupproject.groupproject_backend.repository.UserRepository;
import groupproject.groupproject_backend.repository.converter.TaskConverter;
import groupproject.groupproject_backend.repository.converter.UserConverter;
import groupproject.groupproject_backend.service.RewardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RewardServiceImpl implements RewardService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    public void completeTask(Long userId, Long taskId) {
        User user = UserConverter.toDomain(userRepository.findById(userId).orElseThrow());
        Task task = TaskConverter.toDomain(taskRepository.findById(taskId).orElseThrow());

        user.setVirtualMoney(user.getVirtualMoney() + task.getPayout());
        userRepository.save(UserConverter.toEntity(user));
    }
}