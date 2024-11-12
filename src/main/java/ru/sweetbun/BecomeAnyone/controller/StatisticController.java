package ru.sweetbun.BecomeAnyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.BecomeAnyone.service.EnrollmentService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/profile/statistics")
public class StatisticController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    public ResponseEntity<?> getAllEnrollmentsByStudent() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollmentsByStudent());
    }
}
