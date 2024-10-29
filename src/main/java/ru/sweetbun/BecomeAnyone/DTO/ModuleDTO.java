package ru.sweetbun.BecomeAnyone.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.sweetbun.BecomeAnyone.entity.Lesson;

import java.util.List;

@Data
@AllArgsConstructor
public class ModuleDTO {

    private String title;
    private String description;
    private Integer orderNum;
    private List<LessonDTO> lessons;
}
