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
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.Enrollment;
import ru.sweetbun.becomeanyone.entity.Progress;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.entity.enums.EnrollmentStatus;
import ru.sweetbun.becomeanyone.dto.enrollment.EnrollmentResponse;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.EnrollmentRepository;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EnrollmentServiceImplTests {

    @Mock
    private EnrollmentRepository enrollmentRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private CourseServiceImpl courseServiceImpl;

    @Mock
    private ProgressService progressService;

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private EnrollmentServiceImpl enrollmentServiceImpl;

    private User currentUser;
    private Course course;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        enrollmentServiceImpl = new EnrollmentServiceImpl(enrollmentRepository, modelMapper, courseServiceImpl, progressService,
                securityUtils);

        currentUser = new User();
        course = new Course();
        enrollment = Enrollment.builder().student(currentUser).course(course).enrollmentDate(LocalDate.now())
                .status(EnrollmentStatus.NOT_STARTED).progress(new Progress()).build();

        lenient().when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        lenient().when(courseServiceImpl.fetchCourseById(anyLong())).thenReturn(course);
    }

    @Test
    void createEnrollment_ValidCourseId_ReturnsEnrollment() {
        Progress progress = new Progress();
        when(progressService.createProgress()).thenReturn(progress);
        when(enrollmentRepository.save(any(Enrollment.class))).thenReturn(enrollment);

        EnrollmentResponse createdEnrollment = enrollmentServiceImpl.createEnrollment(1L);

        assertNotNull(createdEnrollment);
        assertEquals(EnrollmentStatus.NOT_STARTED.toString(), createdEnrollment.getStatus());
        verify(enrollmentRepository, times(1)).save(any(Enrollment.class));
    }

    @Test
    void getEnrollmentById_ExistingId_ReturnsEnrollment() {
        when(enrollmentRepository.findById(anyLong())).thenReturn(Optional.of(enrollment));

        Enrollment foundEnrollment = enrollmentServiceImpl.getEnrollmentById(1L);

        assertNotNull(foundEnrollment);
        assertEquals(currentUser, foundEnrollment.getStudent());
    }

    @Test
    void getEnrollmentById_NonExistingId_ThrowsResourceNotFoundException() {
        when(enrollmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> enrollmentServiceImpl.getEnrollmentById(1L));
    }

    @Test
    void getAllEnrollmentsByStudent_ValidStudent_ReturnsEnrollmentsListCurrent() {
        when(enrollmentRepository.findAllByStudent(currentUser)).thenReturn(List.of(enrollment));

        List<EnrollmentResponse> enrollments = enrollmentServiceImpl.getAllEnrollmentsByCurrentStudent();

        assertNotNull(enrollments);
        assertFalse(enrollments.isEmpty());
        assertEquals(1, enrollments.size());
    }

    @ParameterizedTest
    @CsvSource({
            "NOT_STARTED, 0.0, NOT_STARTED",
            "NOT_STARTED, 50.0, IN_PROGRESS",
            "NOT_STARTED, 100.0, COMPLETED",
            "IN_PROGRESS, 50.0, IN_PROGRESS",
            "IN_PROGRESS, 50.0, IN_PROGRESS",
            "IN_PROGRESS, 100.0, COMPLETED",
            "COMPLETED, 100.0, COMPLETED",
            "COMPLETED, 50.0, COMPLETED",
            "COMPLETED, 0.0, COMPLETED"
    })
    void updateEnrollmentStatus_VariousCompletionPercentagesAndStatuses_ReturnsExpectedStatus(
            EnrollmentStatus initialStatus,
            double completionPercent,
            EnrollmentStatus expectedStatus) {

        enrollment.setStatus(initialStatus);
        when(enrollmentRepository.save(any(Enrollment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Enrollment updatedEnrollment = enrollmentServiceImpl.updateEnrollmentStatus(completionPercent, enrollment);

        assertEquals(expectedStatus, updatedEnrollment.getStatus());
        verify(enrollmentRepository, times(1)).save(enrollment);
    }

    @Test
    void deleteEnrollment_ExistingCourse_ReturnsCourseId() {
        when(securityUtils.getCurrentUser()).thenReturn(currentUser);
        when(courseServiceImpl.fetchCourseById(1L)).thenReturn(course);
        when(enrollmentRepository.findByStudentAndCourse(currentUser, course)).thenReturn(Optional.of(enrollment));

        long courseId = enrollmentServiceImpl.deleteEnrollment(1L);

        assertEquals(1L, courseId);
        verify(enrollmentRepository, times(1)).findByStudentAndCourse(currentUser, course);
    }

    @Test
    void deleteEnrollment_NonExistingCourse_ThrowsResourceNotFoundException() {
        when(courseServiceImpl.fetchCourseById(anyLong())).thenThrow(new ResourceNotFoundException(Course.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> enrollmentServiceImpl.deleteEnrollment(1L));
    }

    @Test
    void getEnrollmentByStudentAndCourse_ExistingEnrollment_ReturnsEnrollment() {
        when(enrollmentRepository.findByStudentAndCourse(currentUser, course)).thenReturn(Optional.of(enrollment));

        Enrollment foundEnrollment = enrollmentServiceImpl.getEnrollmentByStudentAndCourse(currentUser, course);

        assertNotNull(foundEnrollment);
        assertEquals(currentUser, foundEnrollment.getStudent());
        assertEquals(course, foundEnrollment.getCourse());
    }

    @Test
    void getEnrollmentByStudentAndCourse_NonExistingEnrollment_ThrowsResourceNotFoundException() {
        when(enrollmentRepository.findByStudentAndCourse(currentUser, course)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> enrollmentServiceImpl.getEnrollmentByStudentAndCourse(currentUser, course));
    }
}