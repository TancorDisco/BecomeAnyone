package ru.sweetbun.becomeanyone.dto.lesson.response;

import lombok.*;
import ru.sweetbun.becomeanyone.dto.content.ContentResponse;
import ru.sweetbun.becomeanyone.dto.test.response.TestResponse;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {

    private Long id;
    private String title;
    private ContentResponse content;
    private int orderNum;
    private List<TestResponse> tests;
}
