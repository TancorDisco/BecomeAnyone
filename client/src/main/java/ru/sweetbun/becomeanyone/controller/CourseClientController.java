package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;
import ru.sweetbun.becomeanyone.service.CourseServiceImpl;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RequestMapping("client/courses")
@RestController
public class CourseClientController {

    private final CourseServiceImpl courseService;

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody CourseRequest<CreateModuleRequest> courseRequest) {
        return ok(courseService.createCourse(courseRequest));
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
    public ResponseEntity<?> updateCourseById(@PathVariable Long id,
                                          @RequestBody CourseRequest<UpdateModuleInCourseRequest> courseRequest) {
        return ok(courseService.updateCourseById(id, courseRequest));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourse(@PathVariable Long id) {
        return ok(courseService.deleteCourseById(id));
    }
}
