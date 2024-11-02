package ru.sweetbun.BecomeAnyone.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QuestionDTO {

    private String questionText;
    private List<AnswerDTO> answers;
}
