package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.contract.EnrollmentService;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Course Enrollment", description = "API для управления записыванием и отчислением от курса")
@RequiredArgsConstructor
@RestController
@RequestMapping("/courses/{courseId}/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Operation(summary = "Записаться на курс", description = "Запись пользователя на указанный курс")
    @PostMapping
    public ResponseEntity<?> enrollInTheCourse(@PathVariable("courseId") Long courseId) {
        return ok(enrollmentService.createEnrollment(courseId));
    }

    @Operation(summary = "Отчислиться от курса", description = "Удаление пользователя из курса")
    @DeleteMapping
    public ResponseEntity<?> dropOutOfTheCourse(@PathVariable("courseId") Long courseId) {
        return ok(enrollmentService.deleteEnrollment(courseId));
    }
}
