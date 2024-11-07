package ru.sweetbun.BecomeAnyone.DTO;

import java.util.List;

public record UpdateModuleInCourseDTO (
        Long id,
        String title,
        int orderNum,
        List<UpdateLessonInCourseDTO> lessons
) implements ModuleDTO{

    public UpdateModuleInCourseDTO {
        if (lessons == null) lessons = List.of();
    }
}
