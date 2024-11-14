package ru.sweetbun.becomeanyone.api.dto;

import java.util.List;

public record ProgressDTO (
        int completedLessons,
        List<Integer> testResults
) {
    public ProgressDTO {
        if (testResults == null) testResults = List.of();
    }
}
