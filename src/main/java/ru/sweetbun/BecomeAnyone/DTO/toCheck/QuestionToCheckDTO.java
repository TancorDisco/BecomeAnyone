package ru.sweetbun.BecomeAnyone.DTO.toCheck;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class QuestionToCheckDTO {

    private Long id;
    private boolean hasSeveralCorrectAnswers;
    private List<AnswerToCheckDTO> answers;
}
