package ru.sweetbun.becomeanyone.dto.question.request;

import lombok.*;
import ru.sweetbun.becomeanyone.dto.answer.request.AnswerRequest;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionRequest<T extends AnswerRequest> {
    private String questionText;
    private boolean hasSeveralCorrectAnswers;
    private String explanation;
    private String imageUrl;
    @Builder.Default
    private List<T> answers = new ArrayList<>();
}
