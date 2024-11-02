package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuestionDTO<T extends AnswerDTO> {

    private String questionText;
    private boolean hasSeveralCorrectAnswers;
    private String explanation;
    private String imageUrl;
    private List<T> answers;
}
