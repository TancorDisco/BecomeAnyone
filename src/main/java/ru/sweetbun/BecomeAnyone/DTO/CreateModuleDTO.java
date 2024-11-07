package ru.sweetbun.BecomeAnyone.DTO;

import java.util.List;

public record CreateModuleDTO (
        String title,
        int orderNum,
        List<CreateLessonDTO> lessons
) implements ModuleDTO {

    public CreateModuleDTO {
        if (lessons == null) lessons = List.of();
    }
}
