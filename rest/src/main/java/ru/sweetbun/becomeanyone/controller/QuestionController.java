package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.aop.CheckCourseOwner;
import ru.sweetbun.becomeanyone.contract.QuestionService;
import ru.sweetbun.becomeanyone.dto.answer.request.CreateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.answer.request.UpdateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.question.request.QuestionRequest;

import static org.springframework.http.ResponseEntity.ok;

@Tag(name = "Question Management", description = "API для управления вопросами тестов")
@RequiredArgsConstructor
@RestController
@RequestMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions")
public class QuestionController {

    private final QuestionService questionService;

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @PostMapping
    @Operation(summary = "Создать вопрос", description = "Создает новый вопрос c вариантами ответа для теста")
    public ResponseEntity<?> createQuestion(@PathVariable("testId") Long testId,
                                            @RequestBody QuestionRequest<CreateAnswerRequest> request) {
        return ok(questionService.createQuestion(request, testId));
    }

    @GetMapping
    @Operation(summary = "Получить все вопросы для теста", description = "Возвращает все вопросы для указанного теста")
    public ResponseEntity<?> getAllQuestionsByTestId(@PathVariable("testId") Long testId) {
        return ok(questionService.getAllQuestionsByTest(testId));
    }

    @GetMapping("{id}")
    @Operation(summary = "Получить вопрос по ID", description = "Возвращает вопрос по его ID")
    public ResponseEntity<?> getQuestionById(@PathVariable("id") Long id) {
        return ok(questionService.getQuestionById(id));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @PatchMapping("{id}")
    @Operation(summary = "Обновить вопрос", description = "Обновляет вопрос с вариантами ответа по его ID")
    public ResponseEntity<?> updateQuestion(@PathVariable("id") Long id,
                                            @RequestBody QuestionRequest<UpdateAnswerRequest> request) {
        return ok(questionService.updateQuestion(request, id));
    }

    @PreAuthorize("hasRole('TEACHER')")
    @CheckCourseOwner
    @DeleteMapping("{id}")
    @Operation(summary = "Удалить вопрос", description = "Удаляет вопрос по его ID")
    public ResponseEntity<?> deleteQuestion(@PathVariable("id") Long id) {
        return ok(questionService.deleteQuestionById(id));
    }
}
