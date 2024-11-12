package ru.sweetbun.becomeanyone.DTO;

import lombok.Builder;

@Builder
public record UpdateLessonDTO (
        String title,
        String content
) {
}
