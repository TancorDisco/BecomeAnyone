package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

import lombok.Builder;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLessonInCourseDTO {
    private Long id;
    private String title;
    private int orderNum;
}
