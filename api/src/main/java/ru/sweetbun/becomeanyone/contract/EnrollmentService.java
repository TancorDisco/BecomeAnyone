package ru.sweetbun.becomeanyone.contract;

import ru.sweetbun.becomeanyone.dto.enrollment.EnrollmentResponse;

public interface EnrollmentService {

    EnrollmentResponse createEnrollment(Long courseId);
    long deleteEnrollment(Long courseId);
}
