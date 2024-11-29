package ru.sweetbun.becomeanyone.dto.question.response;

import lombok.*;
import ru.sweetbun.becomeanyone.dto.answer.response.AnswerResponse;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponse {

    private Long id;
    private String questionText;
    private boolean hasSeveralCorrectAnswers;
    private String explanation;
    private String imageUrl;
    private List<AnswerResponse> answers;
}