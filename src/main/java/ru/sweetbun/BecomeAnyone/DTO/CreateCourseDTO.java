package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCourseDTO {

    private String title;
    private String description;
    private String requirements;
    private String coursePlan;
    private List<CreateModuleDTO> modules;
}
