package groupproject.groupproject_backend.repository.entity;

import groupproject.groupproject_backend.domain.User;
import groupproject.groupproject_backend.domain.enums.DifficultyStatus;
import groupproject.groupproject_backend.domain.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "\"TASK\"")
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String title;
    private String description;
    //private User creators;
    private double payout;
    private double budget;
    private String link;

    @Enumerated(EnumType.STRING)
    private DifficultyStatus difficultyStatus;

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private UserEntity creator;


    @Lob
    @Column(name = "file_data")
    private byte[]  image;

}
