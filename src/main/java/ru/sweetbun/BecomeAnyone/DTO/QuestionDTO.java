package ru.sweetbun.BecomeAnyone.DTO;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sweetbun.BecomeAnyone.entity.Answer;
import ru.sweetbun.BecomeAnyone.entity.Test;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class QuestionDTO {

    private String questionText;
    private List<Answer> answers;
}
