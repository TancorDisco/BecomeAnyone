package ru.sweetbun.become_anyone.DTO;

import lombok.Builder;

@Builder
public record CreateLessonDTO (
        String title,
        int orderNum
) {
}
