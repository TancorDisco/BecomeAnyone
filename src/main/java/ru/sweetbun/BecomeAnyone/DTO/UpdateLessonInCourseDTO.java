package ru.sweetbun.BecomeAnyone.DTO;

import lombok.Builder;

@Builder
public record UpdateLessonInCourseDTO (
        Long id,
        String title,
        int orderNum
) {
}
