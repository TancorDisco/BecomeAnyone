package ru.sweetbun.BecomeAnyone.DTO;

import java.util.List;

public record QuestionDTO<T extends AnswerDTO> (
        String questionText,
        boolean hasSeveralCorrectAnswers,
        String explanation,
        String imageUrl,
        List<T> answers
) {
    public QuestionDTO {
        if (answers == null) answers = List.of();
    }
}
