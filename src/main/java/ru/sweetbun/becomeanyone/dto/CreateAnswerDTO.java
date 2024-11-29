package ru.sweetbun.becomeanyone.dto;

import lombok.Builder;

@Builder
public record CreateAnswerDTO (
        String answerText,
        boolean correct

) implements AnswerDTO {
}
