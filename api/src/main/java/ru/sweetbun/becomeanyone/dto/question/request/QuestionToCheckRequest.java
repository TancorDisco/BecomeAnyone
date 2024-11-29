package ru.sweetbun.becomeanyone.dto.question.request;

import lombok.Builder;
import ru.sweetbun.becomeanyone.dto.answer.request.AnswerToCheckRequest;

import java.util.List;

@Builder
public record QuestionToCheckRequest(
        Long id,
        boolean hasSeveralCorrectAnswers,
        List<AnswerToCheckRequest> answers
) {

    public QuestionToCheckRequest {
        if (answers == null) answers = List.of();
    }
}
