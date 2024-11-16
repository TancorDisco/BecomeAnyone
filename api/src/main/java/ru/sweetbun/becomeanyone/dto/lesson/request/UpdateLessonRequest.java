package ru.sweetbun.becomeanyone.dto.lesson.request;

import lombok.Builder;
import ru.sweetbun.becomeanyone.dto.content.ContentRequest;

@Builder
public record UpdateLessonRequest(
        String title,
        ContentRequest content
) {
}
