package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.aop.CheckCourseOwner;
import ru.sweetbun.becomeanyone.contract.CourseService;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;
import ru.sweetbun.becomeanyone.metric.CourseMetrics;

@Tag(name = "Course Management", description = "API для управления курсами")
@RequiredArgsConstructor
@RequestMapping("/courses")
@RestController
public class CourseController {

    private final CourseService courseService;
    private final CourseMetrics courseMetrics;

    @PreAuthorize("hasRole('TEACHER')")
    @Operation(summary = "Создать курс", description = "Создание нового курса с модулями")
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseRequest<CreateModuleRequest> request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    @Operation(summary = "Получить список курсов", description = "Получение всех курсов с возможностью фильтрации по учителю и строке запроса")
    @GetMapping
    public ResponseEntity<?> getAllCourses(
            @Parameter(description = "ID учителя для фильтрации") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "Текст для поиска курсов") @RequestParam(required = false) String q,
            @Parameter(description = "Страница") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "10") int pageSize) {
        return ResponseEntity.ok(courseService.getAllCourses(teacherId, q, page, pageSize));
    }

    @Operation(summary = "Получить курс по ID", description = "Получение подробной информации о курсе по его идентификатору")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        courseMetrics.incrementCourseView(id);
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @Operation(summary = "Обновить курс по ID", description = "Обновление данных курса, включая его модули и уроки:" +
            " считывает новые, удаляет ненужные, обновляет оставшиеся")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCourseById(
            @PathVariable Long id,
            @RequestBody CourseRequest<UpdateModuleInCourseRequest> request) {
        return ResponseEntity.ok(courseService.updateCourseById(id, request));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @Operation(summary = "Удалить курс по ID", description = "Удаление курса по его идентификатору")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.deleteCourseById(id));
    }
}
