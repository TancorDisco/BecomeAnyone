package ru.sweetbun.BecomeAnyone.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.sweetbun.BecomeAnyone.entity.Test;

import java.util.List;

@Data
@AllArgsConstructor
public class LessonDTO {

    private String title;
    private String content;
    private List<Test> tests;
}
