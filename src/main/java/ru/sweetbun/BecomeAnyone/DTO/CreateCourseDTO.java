package ru.sweetbun.BecomeAnyone.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateCourseDTO {

    private String title;
    private String description;
    private String requirements;
    private String coursePlan;
    private List<CreateModuleDTO> modules;
}
