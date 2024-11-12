package ru.sweetbun.BecomeAnyone.DTO;

import lombok.Builder;

@Builder
public record CreateLessonDTO (
        String title,
        int orderNum
) {
}
