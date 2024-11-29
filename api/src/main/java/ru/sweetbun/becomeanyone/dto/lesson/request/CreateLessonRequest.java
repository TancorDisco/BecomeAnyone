package ru.sweetbun.becomeanyone.dto.lesson.request;

import lombok.Builder;

@Builder
public record CreateLessonRequest(
        String title,
        int orderNum
) {
}
