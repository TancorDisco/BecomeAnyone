package ru.sweetbun.BecomeAnyone.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @Column(name = "enrollment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL)
    private Progress progress;

    @Column(name = "status")
    private String status;
}
