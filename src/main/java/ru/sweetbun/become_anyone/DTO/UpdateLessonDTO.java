package ru.sweetbun.become_anyone.DTO;

import lombok.Builder;

@Builder
public record UpdateLessonDTO (
        String title,
        String content
) {
}
