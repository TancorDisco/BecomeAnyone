package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.becomeanyone.domain.service.CourseService;
import ru.sweetbun.becomeanyone.domain.service.EnrollmentService;
import ru.sweetbun.becomeanyone.domain.service.ProgressService;
import ru.sweetbun.becomeanyone.infrastructure.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.domain.entity.Course;
import ru.sweetbun.becomeanyone.domain.entity.Enrollment;
import ru.sweetbun.becomeanyone.domain.entity.Progress;
import ru.sweetbun.becomeanyone.domain.entity.User;
import ru.sweetbun.becomeanyone.domain.entity.enums.EnrollmentStatus;
import ru.sweetbun.becomeanyone.infrastructure.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.infrastructure.repository.EnrollmentRepository;
import ru.sweetbun.becomeanyone.domain.util.SecurityUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceTests {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private CourseService courseService;

    @Mock
    private ProgressService progressService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private EnrollmentService enrollmentService;

    private User currentUser;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        enrollmentService = new EnrollmentService(enrollmentRepository, modelMapper, courseService, progressService,
                securityUtils);

        currentUser = new User();
        course = new Course();
        enrollment = Enrollment.builder().student(currentUser).course(course).enrollmentDate(LocalDate.now())
                .status(EnrollmentStatus.NOT_STARTED).build();

        lenient().when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        lenient().when(courseService.getCourseById(anyLong())).thenReturn(course);
    }

    @Test
    void createEnrollment_ValidCourseId_ReturnsEnrollment() {
        Progress progress = new Progress();
        when(progressService.createProgress()).thenReturn(progress);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        Enrollment createdEnrollment = enrollmentService.createEnrollment(1L);

        assertNotNull(createdEnrollment);
        assertEquals(EnrollmentStatus.NOT_STARTED, createdEnrollment.getStatus());
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void getEnrollmentById_ExistingId_ReturnsEnrollment() {
        when(enrollmentRepository.findById(anyLong())).thenReturn(Optional.of(enrollment));

        Enrollment foundEnrollment = enrollmentService.getEnrollmentById(1L);

        assertNotNull(foundEnrollment);
        assertEquals(currentUser, foundEnrollment.getStudent());
    }

    @Test
    void getEnrollmentById_NonExistingId_ThrowsResourceNotFoundException() {
        when(enrollmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> enrollmentService.getEnrollmentById(1L));
    }

    @Test
    void getAllEnrollmentsByStudent_ValidStudent_ReturnsEnrollmentsList() {
        when(enrollmentRepository.findAllByStudent(currentUser)).thenReturn(List.of(enrollment));

        List<Enrollment> enrollments = enrollmentService.getAllEnrollmentsByStudent();

        assertNotNull(enrollments);
        assertFalse(enrollments.isEmpty());
        assertEquals(1, enrollments.size());
        assertEquals(currentUser, enrollments.get(0).getStudent());
    }

    @ParameterizedTest
    @CsvSource({
            "NOT_STARTED, 0.0, NOT_STARTED",
            "NOT_STARTED, 50.0, IN_PROGRESS",
            "IN_PROGRESS, 50.0, IN_PROGRESS",
            "IN_PROGRESS, 100.0, COMPLETED",
            "COMPLETED, 100.0, COMPLETED",
            "COMPLETED, 50.0, COMPLETED"
    })
    void updateEnrollmentStatus_VariousCompletionPercentagesAndStatuses_ReturnsExpectedStatus(
            EnrollmentStatus initialStatus,
            double completionPercent,
            EnrollmentStatus expectedStatus) {

        enrollment.setStatus(initialStatus);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Enrollment updatedEnrollment = enrollmentService.updateEnrollmentStatus(completionPercent, enrollment);

        assertEquals(expectedStatus, updatedEnrollment.getStatus());
        verify(enrollmentRepository, times(1)).save(enrollment);
    }

    @Test
    void deleteEnrollment_ExistingCourse_ReturnsCourseId() {
        doNothing().when(enrollmentRepository).deleteByStudentAndCourse(currentUser, course);

        long courseId = enrollmentService.deleteEnrollment(1L);

        assertEquals(1L, courseId);
        verify(enrollmentRepository, times(1)).deleteByStudentAndCourse(currentUser, course);
    }

    @Test
    void deleteEnrollment_NonExistingCourse_ThrowsResourceNotFoundException() {
        when(courseService.getCourseById(anyLong())).thenThrow(new ResourceNotFoundException(Course.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> enrollmentService.deleteEnrollment(1L));
    }

    @Test
    void getEnrollmentByStudentAndCourse_ExistingEnrollment_ReturnsEnrollment() {
        when(enrollmentRepository.findByStudentAndCourse(currentUser, course)).thenReturn(Optional.of(enrollment));

        Enrollment foundEnrollment = enrollmentService.getEnrollmentByStudentAndCourse(currentUser, course);

        assertNotNull(foundEnrollment);
        assertEquals(currentUser, foundEnrollment.getStudent());
        assertEquals(course, foundEnrollment.getCourse());
    }

    @Test
    void getEnrollmentByStudentAndCourse_NonExistingEnrollment_ThrowsResourceNotFoundException() {
        when(enrollmentRepository.findByStudentAndCourse(currentUser, course)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> enrollmentService.getEnrollmentByStudentAndCourse(currentUser, course));
    }
}