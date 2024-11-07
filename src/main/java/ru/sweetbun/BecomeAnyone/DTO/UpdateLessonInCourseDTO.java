package ru.sweetbun.BecomeAnyone.DTO;

public record UpdateLessonInCourseDTO (
        Long id,
        String title,
        int orderNum
) {
}
