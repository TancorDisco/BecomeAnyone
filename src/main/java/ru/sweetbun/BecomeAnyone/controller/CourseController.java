package ru.sweetbun.BecomeAnyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.BecomeAnyone.DTO.CourseDTO;
import ru.sweetbun.BecomeAnyone.DTO.CreateModuleDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateModuleInCourseDTO;
import ru.sweetbun.BecomeAnyone.service.CourseService;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseDTO<CreateModuleDTO> courseDTO) {
        return ok(courseService.createCourse(courseDTO));
    }

    @GetMapping
    public ResponseEntity<?> getAllCourses(@RequestParam(required = false) Long teacherId,
                                           @RequestParam(required = false) String q) {
        return ok(courseService.getAllCourses(teacherId, q));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable Long id) {
        return ok(courseService.getCourseById(id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCourse(@PathVariable Long id,
                                          @RequestBody CourseDTO<UpdateModuleInCourseDTO> courseDTO) {
        return ok(courseService.updateCourse(courseDTO, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        return ok(courseService.deleteCourseById(id));
    }
}
