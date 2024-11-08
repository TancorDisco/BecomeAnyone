package ru.sweetbun.BecomeAnyone.DTO;

import lombok.Builder;

import java.util.List;

@Builder
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
