package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.aop.CheckCourseOwner;
import ru.sweetbun.becomeanyone.contract.LessonService;
import ru.sweetbun.becomeanyone.dto.lesson.request.CreateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonRequest;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Lesson Management", description = "API для управления уроками в модулях курса")
@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}/modules/{moduleId}/lessons")
@RestController
public class LessonController {

    private final LessonService lessonService;

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @Operation(summary = "Создать урок", description = "Создание нового урока в указанном модуле")
    @PostMapping
    public ResponseEntity<?> createLesson(@PathVariable("moduleId") Long moduleId, @RequestBody CreateLessonRequest request) {
        return ok(lessonService.createLesson(request, moduleId));
    }

    @Operation(summary = "Получить все уроки", description = "Получение списка всех уроков для указанного модуля")
    @GetMapping
    public ResponseEntity<?> getAllLessonsByModule(@PathVariable("moduleId") Long moduleId) {
        return ok(lessonService.getAllLessonsByModule(moduleId));
    }

    @Operation(summary = "Получить урок по ID", description = "Получение информации об уроке по его идентификатору")
    @GetMapping("{id}")
    public ResponseEntity<?> getLessonById(@PathVariable("id") Long id) {
        return ok(lessonService.getLessonById(id));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @Operation(summary = "Обновить урок", description = "Обновление данных существующего урока")
    @PatchMapping("{id}")
    public ResponseEntity<?> updateLesson(@PathVariable("id") Long id, @RequestBody UpdateLessonRequest request) {
        return ok(lessonService.updateLesson(request, id));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @Operation(summary = "Удалить урок", description = "Удаление урока по его идентификатору")
    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable("id") Long id) {
        return ok(lessonService.deleteLessonById(id));
    }
}
