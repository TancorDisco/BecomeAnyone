package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.sweetbun.becomeanyone.dto.enrollment.EnrollmentResponse;

import java.util.List;

@FeignClient(name = "enrollmentService", url = "http:/localhost:8080")
public interface EnrollmentServiceClient {

    @PostMapping("/courses/{courseId}/enrollments")
    EnrollmentResponse createEnrollment(@PathVariable Long courseId);

    @DeleteMapping("/courses/{courseId}/enrollments")
    long deleteEnrollment(@PathVariable Long courseId);

    @GetMapping("/profile/statistics")
    List<EnrollmentResponse> getAllEnrollmentsByCurrentStudent();
}
