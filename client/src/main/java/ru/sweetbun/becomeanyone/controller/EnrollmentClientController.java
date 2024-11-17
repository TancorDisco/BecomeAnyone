package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.service.EnrollmentServiceImpl;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequiredArgsConstructor
@RequestMapping("client/courses/{courseId}/enrollments")
public class EnrollmentClientController {

    private final EnrollmentServiceImpl enrollmentService;

    @PostMapping
    public ResponseEntity<?> enrollInTheCourse(@PathVariable("courseId") Long courseId) {
        return ok(enrollmentService.createEnrollment(courseId));
    }

    @DeleteMapping
    public ResponseEntity<?> dropOutOfTheCourse(@PathVariable("courseId") Long courseId) {
        return ok(enrollmentService.deleteEnrollment(courseId));
    }
}
