package ru.sweetbun.becomeanyone.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.becomeanyone.service.EnrollmentServiceImpl;

@RequiredArgsConstructor
@RestController
@RequestMapping("/client/profile/statistics")
public class StatisticClientController {

    private final EnrollmentServiceImpl enrollmentService;

    @GetMapping
    public ResponseEntity<?> getAllEnrollmentsByStudent() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollmentsByCurrentStudent());
    }
}
