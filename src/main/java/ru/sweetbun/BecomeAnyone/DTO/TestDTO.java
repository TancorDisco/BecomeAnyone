package ru.sweetbun.BecomeAnyone.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.sweetbun.BecomeAnyone.entity.Question;

import java.util.List;

@Data
@AllArgsConstructor
public class TestDTO {

    private String title;
    private String description;
    private List<Question> questions;
}
