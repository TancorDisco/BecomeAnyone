package ru.sweetbun.become_anyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.become_anyone.DTO.CreateAnswerDTO;
import ru.sweetbun.become_anyone.DTO.QuestionDTO;
import ru.sweetbun.become_anyone.DTO.UpdateAnswerDTO;
import ru.sweetbun.become_anyone.service.QuestionService;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions")
public class QuestionController {

    private final QuestionService questionService;

    @PostMapping
    public ResponseEntity<?> createQuestion(@PathVariable("testId") Long testId,
                                            @RequestBody QuestionDTO<CreateAnswerDTO> questionDTO) {
        return ok(questionService.createQuestion(questionDTO, testId));
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
                                            @RequestBody QuestionDTO<UpdateAnswerDTO> questionDTO) {
        return ok(questionService.updateQuestion(questionDTO, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteQuestion(@PathVariable("id") Long id) {
        return ok(questionService.deleteQuestionById(id));
    }
}