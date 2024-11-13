package ru.sweetbun.becomeanyone.DTO;

import lombok.Builder;

@Builder
public record CreateAnswerDTO (
        String answerText,
        boolean correct

) implements AnswerDTO {
}
