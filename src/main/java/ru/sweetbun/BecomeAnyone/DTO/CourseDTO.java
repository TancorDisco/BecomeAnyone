package ru.sweetbun.BecomeAnyone.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.sweetbun.BecomeAnyone.entity.Module;

import java.util.List;

@Data
@AllArgsConstructor
public class CourseDTO {

    private String title;
    private String description;
    private String requirements;
    private String coursePlan;
    private List<Module> modules;
}
