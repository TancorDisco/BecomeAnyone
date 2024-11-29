package ru.sweetbun.becomeanyone.dto.test.request;

import ru.sweetbun.becomeanyone.dto.question.request.QuestionToCheckRequest;

import java.util.List;

public record TestToCheckRequest(List<QuestionToCheckRequest> questions){

    public TestToCheckRequest {
        if (questions == null) questions = List.of();
    }
}
