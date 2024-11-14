package ru.sweetbun.becomeanyone.dto;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO<T extends ModuleDTO> {
    private String title;
    private String description;
    private String requirements;
    private String coursePlan;
    @Builder.Default
    private List<T> modules = new ArrayList<>();
}