package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sweetbun.becomeanyone.entity.*;
import ru.sweetbun.becomeanyone.repository.TestResultRepository;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestResultServiceTests {

    @Mock
    private TestResultRepository testResultRepository;

    @Mock
    private SecurityUtils securityUtils;

    @Mock
    private EnrollmentServiceImpl enrollmentServiceImpl;

    @Mock
    private CourseServiceImpl courseServiceImpl;

    @Mock
    private ProgressService progressService;

    private TestResultService testResultService;

    private final double acceptablePercentage = 75.0;

    private ru.sweetbun.becomeanyone.entity.Test test;
    private User user;
    private Course course;
    private Progress progress;
    private Enrollment enrollment;

    @BeforeEach
    void setUp() {
        testResultService = new TestResultService(testResultRepository, securityUtils,
                enrollmentServiceImpl, courseServiceImpl, progressService, acceptablePercentage);

        test = new ru.sweetbun.becomeanyone.entity.Test();
        user = new User();
        course = new Course();
        progress = new Progress();
        enrollment = Enrollment.builder().progress(progress).build();
    }

    @Test
    void createTestResult_ValidInput_SavesTestResult() {
        double percent = 85.0;
        Long courseId = 1L;
        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(courseServiceImpl.fetchCourseById(courseId)).thenReturn(course);
        when(enrollmentServiceImpl.getEnrollmentByStudentAndCourse(user, course)).thenReturn(enrollment);
        when(testResultRepository.save(any(TestResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TestResult result = testResultService.createTestResult(test, percent, courseId);

        assertNotNull(result);
        assertEquals(percent, result.getPercent());
        assertEquals(test, result.getTest());
        assertEquals(progress, result.getProgress());
        verify(testResultRepository, times(1)).save(result);
    }

    @Test
    void createTestResult_PercentageBelowAcceptable_DoesNotUpdateProgress() {
        double percent = 50.0;
        Long courseId = 1L;

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(courseServiceImpl.fetchCourseById(courseId)).thenReturn(course);
        when(enrollmentServiceImpl.getEnrollmentByStudentAndCourse(user, course)).thenReturn(enrollment);
        when(testResultRepository.save(any(TestResult.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(testResultRepository.existsByTestAndProgressAndPercentGreaterThanEqual(test, progress, acceptablePercentage)).thenReturn(true);

        TestResult result = testResultService.createTestResult(test, percent, courseId);

        assertNotNull(result);
        verify(progressService, never()).updateProgress(any(TestResult.class), any(Course.class));
        verify(enrollmentServiceImpl, never()).updateEnrollmentStatus(anyDouble(), eq(enrollment));
    }
}