package ru.sweetbun.becomeanyone.dto.testresult;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultResponse {

    private Long id;
    private double percent;
}
