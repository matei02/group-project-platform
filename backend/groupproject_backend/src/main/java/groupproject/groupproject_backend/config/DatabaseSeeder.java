package groupproject.groupproject_backend.config;

import groupproject.groupproject_backend.domain.Task;
import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.domain.enums.DifficultyStatus;
import groupproject.groupproject_backend.domain.enums.TaskStatus;
import groupproject.groupproject_backend.repository.TaskRepository;
import groupproject.groupproject_backend.repository.UserRepository;
import groupproject.groupproject_backend.repository.converter.UserConverter;
import groupproject.groupproject_backend.repository.entity.TaskEntity;
import groupproject.groupproject_backend.repository.entity.UserEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final TaskRepository taskRepository;


    public DatabaseSeeder(UserRepository userRepository, TaskRepository taskRepository) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            UserEntity user1 = UserEntity.builder()
                    .id(null)
                    .username("rob")
                    .virtualMoney(20).build();
            userRepository.save(user1);
            System.out.println("Inserted hardcoded user1: " + user1.getUsername());

            UserEntity user2 = UserEntity.builder()
                    .id(null)
                    .username("bob")
                    .virtualMoney(100).build();
            userRepository.save(user2);
            System.out.println("Inserted hardcoded user2: " + user2.getUsername());

            // Now create and insert a hardcoded task for the user
            TaskEntity task1 = TaskEntity.builder()
                    .title("Sample Task Title 1")
                    .description("This is a sample task 1 description.")
                    .payout(2.0)
                    .budget(10.0)
                    .link("this is a link")
                    .difficultyStatus(DifficultyStatus.LOW)
                    .status(TaskStatus.ACCEPTED)
                    .creator(user1)
                    .build();
            taskRepository.save(task1);
            System.out.println("Inserted hardcoded task1: " + task1.getTitle());

            // Now create and insert a hardcoded task for the user
            TaskEntity task2 = TaskEntity.builder()
                    .title("Sample Task Title 2")
                    .description("This is a sample task 2 description.")
                    .payout(2.0)
                    .budget(10.0)
                    .link("this is a link")
                    .status(TaskStatus.PENDING)
                    .difficultyStatus(DifficultyStatus.LOW)
                    .creator(user1)
                    .build();
            taskRepository.save(task2);
            System.out.println("Inserted hardcoded task2: " + task2.getTitle());

            // Now create and insert a hardcoded task for the user
            TaskEntity task3 = TaskEntity.builder()
                    .title("Sample Task Title 3")
                    .description("This is a sample task 3 description.")
                    .payout(2.0)
                    .budget(10.0)
                    .link("this is a link")
                    .difficultyStatus(DifficultyStatus.LOW)
                    .status(TaskStatus.PENDING)
                    .creator(user2)
                    .build();
            taskRepository.save(task3);
            System.out.println("Inserted hardcoded task3: " + task3.getTitle());

        }
    }
}
