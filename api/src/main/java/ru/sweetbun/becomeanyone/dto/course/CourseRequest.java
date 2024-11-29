package ru.sweetbun.becomeanyone.dto.course;

import lombok.*;
import ru.sweetbun.becomeanyone.dto.module.request.ModuleRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequest<T extends ModuleRequest> {
    private String title;
    private String description;
    private String requirements;
    private String coursePlan;
    @Builder.Default
    private List<T> modules = new ArrayList<>();
}