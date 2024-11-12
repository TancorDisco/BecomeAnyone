package ru.sweetbun.BecomeAnyone.DTO;

import lombok.Builder;

@Builder
public record UpdateLessonDTO (
        String title,
        String content
) {
}
