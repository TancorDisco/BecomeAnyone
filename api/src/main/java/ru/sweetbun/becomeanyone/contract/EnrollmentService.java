package ru.sweetbun.becomeanyone.contract;

import org.springframework.data.domain.Page;
import ru.sweetbun.becomeanyone.dto.enrollment.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse createEnrollment(Long courseId);
    long deleteEnrollment(Long courseId);
    List<EnrollmentResponse> getAllEnrollmentsByCurrentStudent();
    Page<EnrollmentResponse> getAllEnrollmentsByCourse(Long courseId, int page, int pageSize);
}
