package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateLessonDTO {

    private Long id;
    private String title;
    private int orderNum;
}
