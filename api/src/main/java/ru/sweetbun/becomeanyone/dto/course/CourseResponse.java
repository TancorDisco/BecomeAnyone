package ru.sweetbun.becomeanyone.dto.course;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import ru.sweetbun.becomeanyone.dto.module.response.ModuleResponse;
import ru.sweetbun.becomeanyone.dto.user.response.UserResponse;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponse {

    private Long id;
    @JsonBackReference
    private UserResponse teacher;
    private String title;
    private String description;
    private String requirements;
    private String coursePlan;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private List<ModuleResponse> modules;
}
