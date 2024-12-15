package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.contract.EnrollmentService;
import ru.sweetbun.becomeanyone.dto.enrollment.EnrollmentResponse;
import ru.sweetbun.becomeanyone.feign.EnrollmentServiceClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentServiceClient enrollmentServiceClient;

    @Override
    public EnrollmentResponse createEnrollment(Long courseId) {
        return enrollmentServiceClient.createEnrollment(courseId);
    }

    @Override
    public long deleteEnrollment(Long courseId) {
        return enrollmentServiceClient.deleteEnrollment(courseId);
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollmentsByCurrentStudent() {
        return enrollmentServiceClient.getAllEnrollmentsByCurrentStudent();
    }

    @Override
    public Page<EnrollmentResponse> getAllEnrollmentsByCourse(Long courseId, int page, int pageSize) {
        return enrollmentServiceClient.getAllEnrollmentsByCourse(courseId, page, pageSize);
    }
}
