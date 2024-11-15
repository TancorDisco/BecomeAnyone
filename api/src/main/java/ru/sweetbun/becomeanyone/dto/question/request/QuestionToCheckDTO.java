package ru.sweetbun.becomeanyone.dto.question.request;

import lombok.Builder;
import ru.sweetbun.becomeanyone.dto.answer.request.AnswerToCheckDTO;

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
