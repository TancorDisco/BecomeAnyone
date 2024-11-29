package ru.sweetbun.becomeanyone.dto.module.response;

import lombok.*;
import ru.sweetbun.becomeanyone.dto.lesson.response.LessonResponse;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleResponse {

    private Long id;
    private String title;
    private String description;
    private int orderNum;
    private List<LessonResponse> lessons;
}
