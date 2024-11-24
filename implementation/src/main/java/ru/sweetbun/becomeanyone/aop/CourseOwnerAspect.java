package ru.sweetbun.becomeanyone.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import ru.sweetbun.becomeanyone.service.CourseServiceImpl;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

@RequiredArgsConstructor
@Aspect
@Component
public class CourseOwnerAspect {

    private final CourseServiceImpl courseService;

    private final SecurityUtils securityUtils;

    @Before("@annotation(CheckCourseOwner) && args(courseId,..)")
    public void checkCourseOwnership(Long courseId) {
        String username = securityUtils.getCurrentUsername();
        if (!courseService.isCourseOwner(courseId, username)) {
            throw new AccessDeniedException("You are not allowed to edit this course");
        }
    }
}
