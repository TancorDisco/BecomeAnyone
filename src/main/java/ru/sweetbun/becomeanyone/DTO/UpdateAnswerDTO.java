package ru.sweetbun.becomeanyone.DTO;

import lombok.Builder;

@Builder
public record UpdateAnswerDTO (
        Long id,
        String answerText,
        boolean correct
) implements AnswerDTO{
}
