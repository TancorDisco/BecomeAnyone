package ru.sweetbun.becomeanyone.dto.test.response;

import lombok.*;
import ru.sweetbun.becomeanyone.dto.question.response.QuestionResponse;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResponse {

    private Long id;
    private String title;
    private String description;
    private List<QuestionResponse> questions;
}
