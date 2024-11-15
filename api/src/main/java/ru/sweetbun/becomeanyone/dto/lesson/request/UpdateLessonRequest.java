package ru.sweetbun.becomeanyone.dto.lesson.request;

import lombok.Builder;
import ru.sweetbun.becomeanyone.dto.content.ContentDTO;

@Builder
public record UpdateLessonRequest(
        String title,
        ContentDTO content
) {
}
