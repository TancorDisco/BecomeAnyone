package ru.sweetbun.BecomeAnyone.DTO;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.entity.Test;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class LessonDTO {

    private String title;
    private String content;
    private Integer orderNum;
    private List<Test> tests;
}
