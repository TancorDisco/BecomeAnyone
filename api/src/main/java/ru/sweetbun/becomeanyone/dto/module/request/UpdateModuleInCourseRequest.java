package ru.sweetbun.becomeanyone.dto.module.request;

import lombok.*;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonInCourseRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateModuleInCourseRequest implements ModuleRequest {
    private Long id;
    private String title;
    private int orderNum;
    @Builder.Default
    private List<UpdateLessonInCourseRequest> lessons = new ArrayList<>();
}
