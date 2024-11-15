package ru.sweetbun.becomeanyone.dto.lesson.request;

import lombok.*;

import lombok.Builder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLessonInCourseRequest {
    private Long id;
    private String title;
    private int orderNum;
}
