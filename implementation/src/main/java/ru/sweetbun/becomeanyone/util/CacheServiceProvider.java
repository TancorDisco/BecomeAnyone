package ru.sweetbun.becomeanyone.util;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.sweetbun.becomeanyone.entity.Lesson;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.repository.CourseRepository;
import ru.sweetbun.becomeanyone.repository.ModuleRepository;

import java.util.List;

@RequiredArgsConstructor
@Component
public class CacheServiceProvider {

    private final CacheService cacheService;

    private final ModuleRepository moduleRepository;

    private final CourseRepository courseRepository;

    public void evictCourseCacheById(Long courseId) {
        cacheService.evictCourseCache(courseId);
    }

    public void evictCourseCacheByModuleId(Long moduleId) {
        Long courseId = moduleRepository.findCourseIdByModuleId(moduleId);
        evictCourseCacheById(courseId);
    }

    public void evictCourseCacheByLesson(Lesson lesson) {
        evictCourseCacheByModuleId(lesson.getModule().getId());
    }

    public void evictCourseCacheByUser(User user) {
        List<Long> courseIds = courseRepository.findCourseIdsByTeacherId(user.getId());
        courseIds.forEach(this::evictCourseCacheById);
    }
}
