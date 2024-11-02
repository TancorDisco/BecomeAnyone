package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLessonInCourseDTO {

    private Long id;
    private String title;
    private int orderNum;
}
