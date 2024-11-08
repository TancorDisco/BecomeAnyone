package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO<T extends AnswerDTO> {
    private String questionText;
    private boolean hasSeveralCorrectAnswers;
    private String explanation;
    private String imageUrl;
    @Builder.Default
    private List<T> answers = new ArrayList<>();
}
