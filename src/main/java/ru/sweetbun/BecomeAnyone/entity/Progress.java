package ru.sweetbun.BecomeAnyone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "progress")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Progress {

    @Id
    @Column(name = "progress_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @Column(name = "completed_lessons")
    private int completedLessons;

    @ElementCollection
    private List<Integer> testResults;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;
}
