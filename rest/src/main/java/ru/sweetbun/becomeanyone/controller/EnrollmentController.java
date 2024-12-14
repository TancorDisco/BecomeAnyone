package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.aop.CheckCourseOwner;
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

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @Operation(summary = "Получить зачисления по курсу", description = "Учитель может посмотреть информацию об ученике")
    @GetMapping
    public ResponseEntity<?> getAllEnrollmentsByCourse(
            @PathVariable("courseId") Long courseId,
            @Parameter(description = "Страница") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int pageSize) {
        return ok(enrollmentService.getAllEnrollmentsByCourse(courseId, page, pageSize));
    }
}
