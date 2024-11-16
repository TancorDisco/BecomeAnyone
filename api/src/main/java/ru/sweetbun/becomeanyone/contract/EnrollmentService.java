package ru.sweetbun.becomeanyone.contract;

import ru.sweetbun.becomeanyone.dto.enrollment.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse createEnrollment(Long courseId);
    long deleteEnrollment(Long courseId);
    List<EnrollmentResponse> getAllEnrollmentsByCurrentStudent();
}
