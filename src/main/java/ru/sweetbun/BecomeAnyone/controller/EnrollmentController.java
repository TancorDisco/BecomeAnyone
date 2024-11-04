package ru.sweetbun.BecomeAnyone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.BecomeAnyone.service.EnrollmentService;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/courses/{courseId}/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @Autowired
    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<?> enrollInTheCourse(@PathVariable("courseId") Long courseId) {
        return ok(enrollmentService.createEnrollment(courseId));
    }

    @DeleteMapping
    public ResponseEntity<?> dropOutOfTheCourse(@PathVariable("courseId") Long courseId) {
        return ok(enrollmentService.deleteEnrollment(courseId));
    }
}
