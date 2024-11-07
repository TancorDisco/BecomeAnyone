package ru.sweetbun.BecomeAnyone.DTO;

import java.util.List;

public record CourseDTO<T extends ModuleDTO>(
        String title,
        String description,
        String requirements,
        String coursePlan,
        List<T> modules
) {
    public CourseDTO {
        if (modules == null) modules = List.of();
    }
}
