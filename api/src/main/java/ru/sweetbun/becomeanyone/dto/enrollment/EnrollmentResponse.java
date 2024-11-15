package ru.sweetbun.becomeanyone.dto.enrollment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import ru.sweetbun.becomeanyone.dto.course.CourseResponse;
import ru.sweetbun.becomeanyone.dto.progress.ProgressResponse;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {

    private Long id;
    @JsonBackReference
    private UserResponse user;
    @JsonManagedReference
    private CourseResponse course;
    private LocalDate enrollmentDate;
    private ProgressResponse progress;
    private String status;
}
