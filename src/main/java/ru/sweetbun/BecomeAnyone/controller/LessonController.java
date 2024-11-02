package ru.sweetbun.BecomeAnyone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.BecomeAnyone.DTO.CreateLessonDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateLessonDTO;
import ru.sweetbun.BecomeAnyone.service.LessonService;

@RequestMapping("/courses/{courseId}/modules/{moduleId}/lessons")
@RestController
public class LessonController {

    private final LessonService lessonService;

    @Autowired
    public LessonController(LessonService lessonService) {
        this.lessonService = lessonService;
    }

    @PostMapping
    public ResponseEntity<?> createLesson(@PathVariable("moduleId") Long moduleId, @RequestBody CreateLessonDTO lessonDTO) {
        return ResponseEntity.ok(lessonService.createLesson(lessonDTO, moduleId));
    }

    @GetMapping
    public ResponseEntity<?> getAllLessonsByModule(@PathVariable("moduleId") Long moduleId) {
        return ResponseEntity.ok(lessonService.getAllLessonsByModule(moduleId));
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getLessonById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(lessonService.getLessonById(id));
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateLesson(@PathVariable("id") Long id, @RequestBody UpdateLessonDTO updateLessonDTO) {
        return ResponseEntity.ok(lessonService.updateLesson(updateLessonDTO, id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteLesson(@PathVariable("id") Long id) {
        return ResponseEntity.ok(lessonService.deleteLessonById(id));
    }
}
