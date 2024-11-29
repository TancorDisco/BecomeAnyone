package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.contract.CourseService;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;

@Tag(name = "Course Management", description = "API для управления курсами")
@RequiredArgsConstructor
@RequestMapping("client/courses")
@RestController
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Создать курс", description = "Создание нового курса с модулями")
    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseRequest<CreateModuleRequest> request) {
        return ResponseEntity.ok(courseService.createCourse(request));
    }

    @Operation(summary = "Получить список курсов", description = "Получение всех курсов с возможностью фильтрации по учителю и строке запроса")
    @GetMapping
    public ResponseEntity<?> getAllCourses(
            @Parameter(description = "ID учителя для фильтрации") @RequestParam(required = false) Long teacherId,
            @Parameter(description = "Текст для поиска курсов") @RequestParam(required = false) String q) {
        return ResponseEntity.ok(courseService.getAllCourses(teacherId, q));
    }

    @Operation(summary = "Получить курс по ID", description = "Получение подробной информации о курсе по его идентификатору")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @Operation(summary = "Обновить курс по ID", description = "Обновление данных курса, включая его модули и уроки:" +
            " считывает новые, удаляет ненужные, обновляет оставшиеся")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCourseById(
            @PathVariable Long id,
            @RequestBody CourseRequest<UpdateModuleInCourseRequest> request) {
        return ResponseEntity.ok(courseService.updateCourseById(id, request));
    }

    @Operation(summary = "Удалить курс по ID", description = "Удаление курса по его идентификатору")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.deleteCourseById(id));
    }
}
