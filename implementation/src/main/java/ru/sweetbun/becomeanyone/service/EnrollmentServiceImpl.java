package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.contract.EnrollmentService;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.Enrollment;
import ru.sweetbun.becomeanyone.entity.Progress;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.dto.enrollment.EnrollmentResponse;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.EnrollmentRepository;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

import java.time.LocalDate;
import java.util.List;

import static ru.sweetbun.becomeanyone.entity.enums.EnrollmentStatus.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    private final ModelMapper modelMapper;

    private final CourseServiceImpl courseServiceImpl;
    @Lazy
    private final ProgressService progressService;

    private final SecurityUtils securityUtils;

    @Override
    @Transactional
    public EnrollmentResponse createEnrollment(Long courseId) {
        Progress progress = progressService.createProgress();
        Enrollment enrollment = Enrollment.builder()
                .student(securityUtils.getCurrentUser())
                .course(courseServiceImpl.fetchCourseById(courseId))
                .enrollmentDate(LocalDate.now())
                .progress(progress)
                .status(NOT_STARTED)
                .build();
        progress.setEnrollment(enrollment);
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return modelMapper.map(savedEnrollment, EnrollmentResponse.class);
    }

    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Enrollment.class, id));
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollmentsByCurrentStudent() {
        return enrollmentRepository.findAllByStudent(securityUtils.getCurrentUser()).stream()
                .map(enrollment -> modelMapper.map(enrollment, EnrollmentResponse.class))
                .toList();
    }

    @Transactional
    public Enrollment updateEnrollmentStatus(double completionPercent, Enrollment enrollment) {
        if (enrollment.getStatus() == NOT_STARTED && completionPercent > 0.0) {
            enrollment.setStatus(IN_PROGRESS);
        } else if (enrollment.getStatus() != COMPLETED && completionPercent == 100.0) {
            enrollment.setStatus(COMPLETED);
        }
        return enrollmentRepository.save(enrollment);
    }

    @Override
    @Transactional
    public long deleteEnrollment(Long courseId) {
        User student = securityUtils.getCurrentUser();
        Course course = courseServiceImpl.fetchCourseById(courseId);
        Enrollment enrollment = getEnrollmentByStudentAndCourse(student, course);
        enrollmentRepository.delete(enrollment);
        return courseId;
    }

    public Enrollment getEnrollmentByStudentAndCourse(User student, Course course) {
        return enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new ResourceNotFoundException(Enrollment.class, student, course));
    }
}
