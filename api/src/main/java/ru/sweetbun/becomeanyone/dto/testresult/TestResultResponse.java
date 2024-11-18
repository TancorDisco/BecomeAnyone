package ru.sweetbun.becomeanyone.dto.testresult;

import lombok.*;
import ru.sweetbun.becomeanyone.dto.test.response.TestResponse;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultResponse {

    private Long id;
    private double percent;
}
