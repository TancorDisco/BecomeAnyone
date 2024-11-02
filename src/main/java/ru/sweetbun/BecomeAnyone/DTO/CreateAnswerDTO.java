package ru.sweetbun.BecomeAnyone.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateAnswerDTO implements AnswerDTO{

    private String answerText;
    private boolean isCorrect;
}
