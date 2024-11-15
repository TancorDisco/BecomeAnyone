package ru.sweetbun.becomeanyone.dto;

import lombok.Builder;

@Builder
public record CreateLessonDTO (
        String title,
        int orderNum
) {
}
