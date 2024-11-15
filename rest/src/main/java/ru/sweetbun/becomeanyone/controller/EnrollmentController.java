package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.contract.EnrollmentService;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@RequestMapping("/courses/{courseId}/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping
    public ResponseEntity<?> enrollInTheCourse(@PathVariable("courseId") Long courseId) {
        return ok(enrollmentService.createEnrollment(courseId));
    }

    @DeleteMapping
    public ResponseEntity<?> dropOutOfTheCourse(@PathVariable("courseId") Long courseId) {
        return ok(enrollmentService.deleteEnrollment(courseId));
    }
}
