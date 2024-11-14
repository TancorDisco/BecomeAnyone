package ru.sweetbun.becomeanyone.dto;

import java.util.List;

public record ProgressDTO (
        int completedLessons,
        List<Integer> testResults
) {
    public ProgressDTO {
        if (testResults == null) testResults = List.of();
    }
}
