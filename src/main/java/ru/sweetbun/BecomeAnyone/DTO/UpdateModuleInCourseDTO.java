package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateModuleInCourseDTO implements ModuleDTO{

    private Long id;
    private String title;
    private int orderNum;
    private List<UpdateLessonInCourseDTO> lessons = new ArrayList<>();
}
