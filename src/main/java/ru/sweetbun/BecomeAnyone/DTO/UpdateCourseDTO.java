package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCourseDTO {

    private String title;
    private String description;
    private String requirements;
    private String coursePlan;
    private List<UpdateModuleInCourseDTO> modules = new ArrayList<>();
}
