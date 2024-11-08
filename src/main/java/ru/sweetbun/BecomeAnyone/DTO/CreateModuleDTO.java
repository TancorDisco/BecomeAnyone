package ru.sweetbun.BecomeAnyone.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateModuleDTO implements ModuleDTO {
    private String title;
    private int orderNum;
    private List<CreateLessonDTO> lessons = new ArrayList<>();
}