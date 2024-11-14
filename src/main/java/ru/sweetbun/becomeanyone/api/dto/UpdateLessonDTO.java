package ru.sweetbun.becomeanyone.api.dto;

import lombok.Builder;

@Builder
public record UpdateLessonDTO (
        String title,
        ContentDTO content
) {
}
