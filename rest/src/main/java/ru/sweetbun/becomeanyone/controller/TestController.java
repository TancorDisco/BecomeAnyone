package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.aop.CheckCourseOwner;
import ru.sweetbun.becomeanyone.contract.TestService;
import ru.sweetbun.becomeanyone.dto.test.request.TestRequest;
import ru.sweetbun.becomeanyone.dto.test.request.TestToCheckRequest;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Test Management", description = "API для управления тестами на уроках")
@RequiredArgsConstructor
@RestController
@RequestMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests")
public class TestController {

    private final TestService testService;

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @PostMapping
    @Operation(summary = "Создать тест", description = "Создает новый тест для указанного урока (без вопросов)")
    public ResponseEntity<?> createTest(@PathVariable("lessonId") Long lessonId, @RequestBody TestRequest testRequest) {
        return ok(testService.createTest(testRequest, lessonId));
    }

    @GetMapping
    @Operation(summary = "Получить все тесты для урока", description = "Возвращает все тесты, связанные с конкретным уроком")
    public ResponseEntity<?> getAllTestsByLesson(@PathVariable("lessonId") Long lessonId) {
        return ok(testService.getAllTestsByLesson(lessonId));
    }

    @GetMapping("{id}")
    @Operation(summary = "Получить тест по ID", description = "Возвращает тест по его ID")
    public ResponseEntity<?> getTestById(@PathVariable("id") Long id) {
        return ok(testService.getTestById(id));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @PatchMapping("{id}")
    @Operation(summary = "Обновить тест", description = "Обновляет информацию о тесте по его ID")
    public ResponseEntity<?> updateTest(@PathVariable("id") Long id, @RequestBody TestRequest testRequest) {
        return ok(testService.updateTest(testRequest, id));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @DeleteMapping("{id}")
    @Operation(summary = "Удалить тест", description = "Удаляет тест по его ID")
    public ResponseEntity<?> deleteTest(@PathVariable("id") Long id) {
        return ok(testService.deleteTestById(id));
    }

    @PostMapping("{id}/check")
    @Operation(summary = "Проверить тест", description = "Проверяет тест по ID, возвращает вопросы где допущена ошибка" +
            " и результаты теста")
    public ResponseEntity<?> checkTest(@PathVariable("id") Long id, @RequestBody TestToCheckRequest testDTO,
                                       @PathVariable("courseId") Long courseId) {
        return ok(testService.checkTest(testDTO, id, courseId));
    }
}
