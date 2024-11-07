package ru.sweetbun.BecomeAnyone.DTO.toCheck;

import java.util.List;

public record TestToCheckDTO (List<QuestionToCheckDTO> questions){

    public TestToCheckDTO {
        if (questions == null) questions = List.of();
    }
}
