package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.EnrollmentDTO;
import ru.sweetbun.BecomeAnyone.entity.Enrollment;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.EnrollmentRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    private final ModelMapper modelMapper;

    private final CourseService courseService;

    private final UserService userService;

    @Autowired
    public EnrollmentService(EnrollmentRepository enrollmentRepository, ModelMapper modelMapper, CourseService courseService, UserService userService) {
        this.enrollmentRepository = enrollmentRepository;
        this.modelMapper = modelMapper;
        this.courseService = courseService;
        this.userService = userService;
    }

    public Enrollment createEnrollment(EnrollmentDTO enrollmentDTO, Long courseId) {
        Enrollment enrollment = modelMapper.map(enrollmentDTO, Enrollment.class);
        enrollment.setCourse(courseService.getCourseById(courseId));
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        enrollment.setStudent(userService.getUserByUsername(username));
        enrollment.setEnrollmentDate(LocalDate.now());
        enrollment.setStatus("IN PROGRESS");

        return enrollmentRepository.save(enrollment);
    }

    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Enrollment.class.getSimpleName(), id));
    }

    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    public Enrollment updateEnrollment(EnrollmentDTO enrollmentDTO, Long id) {
        Enrollment enrollment = getEnrollmentById(id);
        enrollment = modelMapper.map(enrollmentDTO, Enrollment.class);
        return enrollmentRepository.save(enrollment);
    }

    public void deleteEnrollmentById(Long id) {
        enrollmentRepository.deleteById(id);
    }
}
