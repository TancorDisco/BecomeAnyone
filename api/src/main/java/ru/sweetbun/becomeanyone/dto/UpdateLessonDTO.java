package ru.sweetbun.becomeanyone.dto;

import lombok.Builder;

@Builder
public record UpdateLessonDTO (
        String title,
        ContentDTO content
) {
}
