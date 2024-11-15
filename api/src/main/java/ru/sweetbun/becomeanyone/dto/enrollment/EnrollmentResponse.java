package ru.sweetbun.becomeanyone.dto.enrollment;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private Long id;
    //private CourseDTO course;
    private LocalDate enrollmentDate;
    //private ProgressDTO progress;
    private String status;
}
