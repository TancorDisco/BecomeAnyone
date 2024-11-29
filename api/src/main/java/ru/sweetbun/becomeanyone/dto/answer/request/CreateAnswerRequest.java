package ru.sweetbun.becomeanyone.dto.answer.request;

import lombok.Builder;

@Builder
public record CreateAnswerRequest(
        String answerText,
        boolean correct

) implements AnswerRequest {
}
