package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.contract.QuestionService;
import ru.sweetbun.becomeanyone.dto.answer.request.CreateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.answer.request.UpdateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.question.request.QuestionRequest;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/client/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions")
public class QuestionClientController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<?> createQuestion(@PathVariable("testId") Long testId,
                                            @RequestBody QuestionRequest<CreateAnswerRequest> questionRequest) {
        return ok(questionService.createQuestion(questionRequest, testId));
    }

    @GetMapping
    public ResponseEntity<?> getAllQuestionsByTestId(@PathVariable("testId") Long testId) {
        return ok(questionService.getAllQuestionsByTest(testId));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getQuestionById(@PathVariable("id") Long id) {
        return ok(questionService.getQuestionById(id));
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateQuestion(@PathVariable("id") Long id,
                                            @RequestBody QuestionRequest<UpdateAnswerRequest> questionRequest) {
        return ok(questionService.updateQuestion(questionRequest, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable("id") Long id) {
        return ok(questionService.deleteQuestionById(id));
    }
}
