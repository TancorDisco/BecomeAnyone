package ru.sweetbun.becomeanyone.dto.answer.request;

import lombok.Builder;

@Builder
public record UpdateAnswerRequest(
        Long id,
        String answerText,
        boolean correct
) implements AnswerRequest {
}
