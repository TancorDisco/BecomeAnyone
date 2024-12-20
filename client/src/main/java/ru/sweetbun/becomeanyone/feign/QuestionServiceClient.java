package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.answer.request.CreateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.answer.request.UpdateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.question.request.QuestionRequest;
import ru.sweetbun.becomeanyone.dto.question.response.QuestionResponse;

import java.util.List;

@FeignClient(name = "questionService", url = "http://localhost:8080")
public interface QuestionServiceClient {

    @PostMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions")
    QuestionResponse createQuestion(@RequestBody QuestionRequest<CreateAnswerRequest> questionRequest,
                                    @PathVariable("testId") Long testId);

    @GetMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions")
    List<QuestionResponse> getAllQuestionsByTest(@PathVariable("testId") Long testId);

    @GetMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{id}")
    QuestionResponse getQuestionById(@PathVariable("id") Long id);

    @PatchMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{id}")
    QuestionResponse updateQuestion(@RequestBody QuestionRequest<UpdateAnswerRequest> questionRequest,
                                    @PathVariable("id") Long id);

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests/{testId}/questions/{id}")
    long deleteQuestionById(@PathVariable("id") Long id);
}
