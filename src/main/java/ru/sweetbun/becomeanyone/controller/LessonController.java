package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.CreateLessonDTO;
import ru.sweetbun.becomeanyone.dto.UpdateLessonDTO;
import ru.sweetbun.becomeanyone.service.LessonService;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RequestMapping("/courses/{courseId}/modules/{moduleId}/lessons")
@RestController
public class LessonController {

    private final LessonService lessonService;

    @PostMapping
    public ResponseEntity<?> createLesson(@PathVariable("moduleId") Long moduleId, @RequestBody CreateLessonDTO lessonDTO) {
        return ok(lessonService.createLesson(lessonDTO, moduleId));
    }

    @GetMapping
    public ResponseEntity<?> getAllLessonsByModule(@PathVariable("moduleId") Long moduleId) {
        return ok(lessonService.getAllLessonsByModule(moduleId));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getLessonById(@PathVariable("id") Long id) {
        return ok(lessonService.getLessonById(id));
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateLesson(@PathVariable("id") Long id, @RequestBody UpdateLessonDTO updateLessonDTO) {
        return ok(lessonService.updateLesson(updateLessonDTO, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable("id") Long id) {
        return ok(lessonService.deleteLessonById(id));
    }
}
