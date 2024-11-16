package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.test.request.TestRequest;
import ru.sweetbun.becomeanyone.dto.test.request.TestToCheckRequest;
import ru.sweetbun.becomeanyone.service.TestService;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests")
public class TestController {

    private final TestService testService;

    @PostMapping
    public ResponseEntity<?> createTest(@PathVariable("lessonId") Long lessonId, @RequestBody TestRequest testRequest) {
        return ok(testService.createTest(testRequest, lessonId));
    }

    @GetMapping
    public ResponseEntity<?> getAllTestsByLesson(@PathVariable("lessonId") Long lessonId) {
        return ok(testService.getAllTestsByLesson(lessonId));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getTestById(@PathVariable("id") Long id) {
        return ok(testService.getTestById(id));
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateTest(@PathVariable("id") Long id, @RequestBody TestRequest testRequest) {
        return ok(testService.updateTest(testRequest, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteTest(@PathVariable("id") Long id) {
        return ok(testService.deleteTestById(id));
    }

    @PostMapping("{id}/check")
    public ResponseEntity<?> checkTest(@PathVariable("id") Long id, @RequestBody TestToCheckRequest testDTO,
                                       @PathVariable("courseId") Long courseId) {
        return ok(testService.checkTest(testDTO, id, courseId));
    }
}
