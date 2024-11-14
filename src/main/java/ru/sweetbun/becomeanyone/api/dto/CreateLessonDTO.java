package ru.sweetbun.becomeanyone.api.dto;

import lombok.Builder;

@Builder
public record CreateLessonDTO (
        String title,
        int orderNum
) {
}
