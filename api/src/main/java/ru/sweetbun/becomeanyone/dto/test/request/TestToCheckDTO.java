package ru.sweetbun.becomeanyone.dto.test.request;

import ru.sweetbun.becomeanyone.dto.question.request.QuestionToCheckDTO;

import java.util.List;

public record TestToCheckDTO (List<QuestionToCheckDTO> questions){

    public TestToCheckDTO {
        if (questions == null) questions = List.of();
    }
}
