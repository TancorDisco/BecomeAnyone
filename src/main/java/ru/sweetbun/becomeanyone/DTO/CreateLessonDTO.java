package ru.sweetbun.becomeanyone.DTO;

import lombok.Builder;

@Builder
public record CreateLessonDTO (
        String title,
        int orderNum
) {
}
