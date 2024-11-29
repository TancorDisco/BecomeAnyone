package ru.sweetbun.becomeanyone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sweetbun.becomeanyone.contract.EnrollmentService;

@Tag(name = "Statistics Management", description = "API для получения статистики по записям на курсы")
@RequiredArgsConstructor
@RestController
@RequestMapping("/profile/statistics")
public class StatisticController {

    private final EnrollmentService enrollmentService;

    @GetMapping
    @Operation(summary = "Получить статистику по учёбе студента", description = "Возвращает информацию о всех курсах, на которые записан текущий студент, " +
            "его прогресс в них")
    public ResponseEntity<?> getAllEnrollmentsByStudent() {
        return ResponseEntity.ok(enrollmentService.getAllEnrollmentsByCurrentStudent());
    }
}
