package ru.sweetbun.BecomeAnyone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.entity.*;
import ru.sweetbun.BecomeAnyone.repository.TestResultRepository;
import ru.sweetbun.BecomeAnyone.util.SecurityUtils;

@Transactional
@Service
public class TestResultService {

    private final TestResultRepository testResultRepository;

    private final SecurityUtils securityUtils;

    private final EnrollmentService enrollmentService;

    private final CourseService courseService;

    @Autowired
    public TestResultService(TestResultRepository testResultRepository, SecurityUtils securityUtils,
                             @Lazy EnrollmentService enrollmentService,
                             @Lazy CourseService courseService, CourseService courseService1) {
        this.testResultRepository = testResultRepository;
        this.securityUtils = securityUtils;
        this.enrollmentService = enrollmentService;
        this.courseService = courseService1;
    }

    public TestResult createTestResult(Test test, double percent, Long courseId) {
        User user = securityUtils.getCurrentUser();
        Course course = courseService.getCourseById(courseId);
        Enrollment enrollment = enrollmentService.getEnrollmentByStudentAndCourse(user, course);
        TestResult testResult = TestResult.builder()
                .test(test)
                .progress(enrollment.getProgress())
                .percent(percent)
                .build();
        return testResultRepository.save(testResult);
    }
 }
