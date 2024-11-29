package ru.sweetbun.becomeanyone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "progress")
@Getter
@Setter
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

    @Column(nullable = false)
    private double completionPercentage = 0.0;

    @Column(name = "completed_tests")
    private int completedTests = 0;

    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL)
    private List<TestResult> testResults = new ArrayList<>();

    @Column(name = "completion_date")
    private LocalDateTime completionDate;
}
