package ru.sweetbun.BecomeAnyone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.BecomeAnyone.DTO.TestDTO;
import ru.sweetbun.BecomeAnyone.service.TestService;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests")
public class TestController {

    private final TestService testService;

    @Autowired
    public TestController(TestService testService) {
        this.testService = testService;
    }

    @PostMapping
    public ResponseEntity<?> createTest(@PathVariable("lessonId") Long lessonId, @RequestBody TestDTO testDTO) {
        return ok(testService.createTest(testDTO, lessonId));
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
    public ResponseEntity<?> updateTest(@PathVariable("id") Long id, @RequestBody TestDTO testDTO) {
        return ok(testService.updateTest(testDTO, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteTest(@PathVariable("id") Long id) {
        return ok(testService.deleteTestById(id));
    }
}
