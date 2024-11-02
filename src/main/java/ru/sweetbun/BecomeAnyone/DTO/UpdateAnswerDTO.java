package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAnswerDTO implements AnswerDTO{

    private Long id;
    private String answerText;
    private boolean isCorrect;
}
