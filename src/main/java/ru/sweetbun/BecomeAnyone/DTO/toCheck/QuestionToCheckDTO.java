package ru.sweetbun.BecomeAnyone.DTO.toCheck;

import lombok.Builder;

import java.util.List;

@Builder
public record QuestionToCheckDTO(
        Long id,
        boolean hasSeveralCorrectAnswers,
        List<AnswerToCheckDTO> answers
) {

    public QuestionToCheckDTO {
        if (answers == null) answers = List.of();
    }
}
