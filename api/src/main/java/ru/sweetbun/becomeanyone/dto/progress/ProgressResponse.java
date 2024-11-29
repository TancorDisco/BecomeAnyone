package ru.sweetbun.becomeanyone.dto.progress;

import lombok.*;
import ru.sweetbun.becomeanyone.dto.testresult.TestResultResponse;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressResponse {

    private Long id;
    private double completionPercentage;
    private int completedTests;
    private List<TestResultResponse> testResults;
    private LocalDateTime completionDate;
}
