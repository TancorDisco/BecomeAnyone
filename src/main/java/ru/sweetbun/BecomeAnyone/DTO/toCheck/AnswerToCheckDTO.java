package ru.sweetbun.BecomeAnyone.DTO.toCheck;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AnswerToCheckDTO {

    private Long id;
    private boolean isCorrect;
}
