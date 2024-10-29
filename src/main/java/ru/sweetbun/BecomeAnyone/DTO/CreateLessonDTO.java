package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLessonDTO {

    private String title;
    private Integer orderNum;
}
