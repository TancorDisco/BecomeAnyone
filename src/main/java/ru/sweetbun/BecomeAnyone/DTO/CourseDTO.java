package ru.sweetbun.BecomeAnyone.DTO;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDTO<T extends ModuleDTO> {

    private String title;
    private String description;
    private String requirements;
    private String coursePlan;
    private List<T> modules = new ArrayList<>();
}
