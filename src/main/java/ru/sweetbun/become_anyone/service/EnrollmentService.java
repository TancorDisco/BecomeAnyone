package ru.sweetbun.become_anyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.become_anyone.entity.Course;
import ru.sweetbun.become_anyone.entity.Enrollment;
import ru.sweetbun.become_anyone.entity.Progress;
import ru.sweetbun.become_anyone.entity.User;
import ru.sweetbun.become_anyone.exception.ResourceNotFoundException;
import ru.sweetbun.become_anyone.repository.EnrollmentRepository;
import ru.sweetbun.become_anyone.util.SecurityUtils;

import java.time.LocalDate;
import java.util.List;

import static ru.sweetbun.become_anyone.entity.enums.EnrollmentStatus.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    private final ModelMapper modelMapper;

    private final CourseService courseService;
    @Lazy
    private final ProgressService progressService;

    private final SecurityUtils securityUtils;

    @Transactional
    public Enrollment createEnrollment(Long courseId) {
        Progress progress = progressService.createProgress();
        Enrollment enrollment = Enrollment.builder()
                .student(securityUtils.getCurrentUser())
                .course(courseService.getCourseById(courseId))
                .enrollmentDate(LocalDate.now())
                .progress(progress)
                .status(NOT_STARTED)
                .build();
        progress.setEnrollment(enrollment);
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Enrollment.class, id));
    }

    public List<Enrollment> getAllEnrollmentsByStudent() {
        return enrollmentRepository.findAllByStudent(securityUtils.getCurrentUser());
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

    @Transactional
    public long deleteEnrollment(Long courseId) {
        User student = securityUtils.getCurrentUser();
        Course course = courseService.getCourseById(courseId);
        enrollmentRepository.deleteByStudentAndCourse(student, course);
        return courseId;
    }

    public Enrollment getEnrollmentByStudentAndCourse(User student, Course course) {
        return enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new ResourceNotFoundException(Enrollment.class, student, course));
    }
}
