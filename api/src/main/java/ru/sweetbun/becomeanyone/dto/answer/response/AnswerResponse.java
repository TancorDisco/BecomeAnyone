package ru.sweetbun.becomeanyone.dto.answer.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponse {

    private Long id;
    private String answerText;
    private boolean correct;
}
