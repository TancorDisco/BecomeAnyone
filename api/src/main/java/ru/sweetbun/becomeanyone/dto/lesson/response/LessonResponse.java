package ru.sweetbun.becomeanyone.dto.lesson.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonResponse {

    private Long id;
    private String title;
    //private Content content;
    private int orderNum;
    //private List<Test> tests;
}
