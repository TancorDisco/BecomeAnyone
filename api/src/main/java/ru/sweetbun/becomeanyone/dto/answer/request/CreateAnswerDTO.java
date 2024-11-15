package ru.sweetbun.becomeanyone.dto.answer.request;

import lombok.Builder;

@Builder
public record CreateAnswerDTO (
        String answerText,
        boolean correct

) implements AnswerDTO {
}
