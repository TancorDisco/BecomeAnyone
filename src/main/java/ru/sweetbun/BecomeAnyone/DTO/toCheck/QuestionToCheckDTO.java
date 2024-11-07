package ru.sweetbun.BecomeAnyone.DTO.toCheck;

import java.util.List;

public record QuestionToCheckDTO(
        Long id,
        boolean hasSeveralCorrectAnswers,
        List<AnswerToCheckDTO> answers
) {

    public QuestionToCheckDTO {
        if (answers == null) answers = List.of();
    }
}
