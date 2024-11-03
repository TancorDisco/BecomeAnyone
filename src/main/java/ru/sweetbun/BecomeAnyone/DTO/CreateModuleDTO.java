package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateModuleDTO implements ModuleDTO{

    private String title;
    private Integer orderNum;
    private List<CreateLessonDTO> lessons = new ArrayList<>();
}
