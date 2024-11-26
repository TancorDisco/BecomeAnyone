package ru.sweetbun.becomeanyone.aop;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.service.CourseServiceImpl;
import ru.sweetbun.becomeanyone.service.EnrollmentServiceImpl;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

@RequiredArgsConstructor
@Aspect
@Component
public class AccessControlAspect {

    private final CourseServiceImpl courseService;

    private final EnrollmentServiceImpl enrollmentService;

    private final SecurityUtils securityUtils;

    @Before("@annotation(CheckCourseOwner)")
    public void checkCourseOwnership(JoinPoint joinPoint) {
        HttpServletRequest request = getRequest();
        Long courseId = getCourseIdFromRequest(request);

        String username = securityUtils.getCurrentUsername();
        if (!courseService.isCourseOwner(courseId, username)) {
            throw new AccessDeniedException("You are not allowed to edit this course");
        }
    }

    @Before("@annotation(EnrolledStudentOnly)")
    public void checkEnrollment(JoinPoint joinPoint) {
        HttpServletRequest request = getRequest();
        Long courseId = getCourseIdFromRequest(request);

        Course course = courseService.fetchCourseById(courseId);
        User student = securityUtils.getCurrentUser();
        enrollmentService.getEnrollmentByStudentAndCourse(student, course);
    }

    private HttpServletRequest getRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new IllegalStateException("Could not get HTTP request attributes");
        }
        return attributes.getRequest();
    }

    private Long getCourseIdFromRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        String courseIdString = extractCourseIdFromUri(uri);
        return parseCourseId(courseIdString);
    }

    private String extractCourseIdFromUri(String uri) {
        String[] segments = uri.split("/");
        for (int i = 0; i < segments.length; i++) {
            if ("courses".equals(segments[i]) && i + 1 < segments.length) {
                return segments[i + 1];
            }
        }
        throw new IllegalArgumentException("Course ID not found in URI");
    }

    private Long parseCourseId(String courseIdString) {
        try {
            return Long.valueOf(courseIdString);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid course ID format: " + courseIdString, e);
        }
    }
}
