package ru.sweetbun.BecomeAnyone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.BecomeAnyone.DTO.CreateAnswerDTO;
import ru.sweetbun.BecomeAnyone.DTO.QuestionDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateAnswerDTO;
import ru.sweetbun.BecomeAnyone.service.QuestionService;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions")
public class QuestionController {

    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

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
