package ru.sweetbun.BecomeAnyone.DTO;

public record CreateAnswerDTO (
        String answerText,
        boolean isCorrect

) implements AnswerDTO {
}
