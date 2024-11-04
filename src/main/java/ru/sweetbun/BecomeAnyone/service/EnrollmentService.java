package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.EnrollmentDTO;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.Enrollment;
import ru.sweetbun.BecomeAnyone.entity.Progress;
import ru.sweetbun.BecomeAnyone.entity.User;
import ru.sweetbun.BecomeAnyone.entity.enums.EnrollmentStatus;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.EnrollmentRepository;

import java.time.LocalDate;
import java.util.List;

@Transactional
@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    private final ModelMapper modelMapper;

    private final CourseService courseService;

    private final UserService userService;

    private final ProgressService progressService;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository, ModelMapper modelMapper, CourseService courseService,
                             UserService userService, @Lazy ProgressService progressService) {
        this.enrollmentRepository = enrollmentRepository;
        this.modelMapper = modelMapper;
        this.courseService = courseService;
        this.userService = userService;
        this.progressService = progressService;
    }

    public Enrollment createEnrollment(Long courseId) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        Progress progress = progressService.createProgress();
        Enrollment enrollment = Enrollment.builder()
                .student(userService.getUserByUsername(username))
                .course(courseService.getCourseById(courseId))
                .enrollmentDate(LocalDate.now())
                .progress(progress)
                .status(EnrollmentStatus.NOT_STARTED)
                .build();
        progress.setEnrollment(enrollment);
        return enrollmentRepository.save(enrollment);
    }

    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Enrollment.class.getSimpleName(), id));
    }

    public List<Enrollment> getAllEnrollmentsByStudent() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return enrollmentRepository.findAllByStudent(userService.getUserByUsername(username));
    }

    public Enrollment updateEnrollment(EnrollmentDTO enrollmentDTO, Long id) {
        Enrollment enrollment = getEnrollmentById(id);
        modelMapper.map(enrollmentDTO, enrollment);
        return enrollmentRepository.save(enrollment);
    }

    public String deleteEnrollment(Long courseId) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userService.getUserByUsername(username);
        Course course = courseService.getCourseById(courseId);
        enrollmentRepository.deleteByStudentAndCourse(student, course);
        return "You have dropped out of the course with id: " + courseId;
    }

    public Enrollment getEnrollmentByStudentAndCourse(User student, Course course) {
        return enrollmentRepository.findByStudentAndCourse(student, course)
                .orElseThrow(() -> new ResourceNotFoundException(Enrollment.class.getSimpleName(), student, course));
    }
}
