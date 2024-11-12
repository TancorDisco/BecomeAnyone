package ru.sweetbun.become_anyone.DTO;

import lombok.Builder;

@Builder
public record CreateAnswerDTO (
        String answerText,
        boolean correct

) implements AnswerDTO {
}
