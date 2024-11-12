package ru.sweetbun.become_anyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.become_anyone.DTO.TestDTO;
import ru.sweetbun.become_anyone.DTO.toCheck.TestToCheckDTO;
import ru.sweetbun.become_anyone.service.TestService;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests")
public class TestController {

    private final TestService testService;

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

    @PostMapping("{id}/check")
    public ResponseEntity<?> checkTest(@PathVariable("id") Long id, @RequestBody TestToCheckDTO testDTO,
                                       @PathVariable("courseId") Long courseId) {
        return ok(testService.checkTest(testDTO, id, courseId));
    }
}
