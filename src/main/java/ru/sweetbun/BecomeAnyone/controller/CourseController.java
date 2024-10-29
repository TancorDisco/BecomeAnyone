package ru.sweetbun.BecomeAnyone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.BecomeAnyone.DTO.CreateCourseDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateCourseDTO;
import ru.sweetbun.BecomeAnyone.service.CourseService;

@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CreateCourseDTO createCourseDTO) {
        return ResponseEntity.ok(courseService.createCourse(createCourseDTO));
    }

    @GetMapping
    public ResponseEntity<?> getAllCourses(@RequestParam(required = false) Long teacherId,
                                           @RequestParam(required = false) String q) {
        return ResponseEntity.ok(courseService.getAllCourses(teacherId, q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id, @RequestBody UpdateCourseDTO updateCourseDTO) {
        return ResponseEntity.ok(courseService.updateCourse(updateCourseDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourseById(id);
        return ResponseEntity.ok("Course with id " + id + " has been deleted");
    }
}
