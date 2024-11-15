package ru.sweetbun.becomeanyone.dto.progress;

import java.util.List;

public record ProgressRequest(
        int completedLessons,
        List<Integer> testResults
) {
    public ProgressRequest {
        if (testResults == null) testResults = List.of();
    }
}
