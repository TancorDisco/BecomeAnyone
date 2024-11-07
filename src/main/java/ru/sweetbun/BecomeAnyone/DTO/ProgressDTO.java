package ru.sweetbun.BecomeAnyone.DTO;

import java.util.List;

public record ProgressDTO (
        int completedLessons,
        List<Integer> testResults
) {
    public ProgressDTO {
        if (testResults == null) testResults = List.of();
    }
}
