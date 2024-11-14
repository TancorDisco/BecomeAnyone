package ru.sweetbun.becomeanyone.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

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

    @JsonBackReference
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enrollment_id")
    private Enrollment enrollment;

    @Column(nullable = false)
    private double completionPercentage = 0.0;

    @Column(name = "completed_tests")
    private int completedTests = 0;

    @JsonManagedReference
    @OneToMany(mappedBy = "progress", cascade = CascadeType.ALL)
    private List<TestResult> testResults = new ArrayList<>();

    @Column(name = "completion_date")
    private LocalDateTime completionDate;
}
