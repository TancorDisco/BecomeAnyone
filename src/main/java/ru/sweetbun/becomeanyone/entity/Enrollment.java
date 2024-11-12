package ru.sweetbun.becomeanyone.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import ru.sweetbun.becomeanyone.entity.enums.EnrollmentStatus;

import java.time.LocalDate;

@Builder
@Entity
@Table(name = "enrollments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Enrollment {

    @Id
    @Column(name = "enrollment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonManagedReference
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @JsonManagedReference
    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL)
    private Progress progress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnrollmentStatus status;
}
