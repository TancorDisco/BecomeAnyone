package ru.sweetbun.BecomeAnyone.DTO.toCheck;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TestToCheckDTO {

    private List<QuestionToCheckDTO> questions =  new ArrayList<>();
}
