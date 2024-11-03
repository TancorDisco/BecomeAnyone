package ru.sweetbun.BecomeAnyone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.BecomeAnyone.DTO.CourseDTO;
import ru.sweetbun.BecomeAnyone.DTO.CreateModuleDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateModuleInCourseDTO;
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
    public ResponseEntity<?> createCourse(@RequestBody CourseDTO<CreateModuleDTO> courseDTO) {
        return ResponseEntity.ok(courseService.createCourse(courseDTO));
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
    public ResponseEntity<?> updateCourse(@PathVariable Long id,
                                          @RequestBody CourseDTO<UpdateModuleInCourseDTO> courseDTO) {
        return ResponseEntity.ok(courseService.updateCourse(courseDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourseById(id);
        return ResponseEntity.ok("Course with id " + id + " has been deleted");
    }
}
