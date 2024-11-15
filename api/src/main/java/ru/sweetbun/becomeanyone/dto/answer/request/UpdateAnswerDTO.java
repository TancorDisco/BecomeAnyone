package ru.sweetbun.becomeanyone.dto.answer.request;

import lombok.Builder;

@Builder
public record UpdateAnswerDTO (
        Long id,
        String answerText,
        boolean correct
) implements AnswerDTO {
}
