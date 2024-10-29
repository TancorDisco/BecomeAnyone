package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateModuleDTO {

    private Long id;
    private String title;
    private int orderNum;
    private List<UpdateLessonDTO> lessons = new ArrayList<>();
}
