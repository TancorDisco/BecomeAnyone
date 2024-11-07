package ru.sweetbun.BecomeAnyone.DTO;

public record UpdateAnswerDTO (
        Long id,
        String answerText,
        boolean isCorrect
) implements AnswerDTO{
}
