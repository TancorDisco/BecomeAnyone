package ru.sweetbun.becomeanyone.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateModuleInCourseDTO implements ModuleDTO {
    private Long id;
    private String title;
    private int orderNum;
    @Builder.Default
    private List<UpdateLessonInCourseDTO> lessons = new ArrayList<>();
}
