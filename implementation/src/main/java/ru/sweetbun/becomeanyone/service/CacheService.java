package ru.sweetbun.becomeanyone.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.repository.ModuleRepository;

@Slf4j
@Service
public class CacheService {

    private final ModuleRepository moduleRepository;

    public CacheService(ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    @CacheEvict(value = "courses", key = "#courseId")
    public void evictCourseCache(Long courseId) {
        log.info("Cache Course deleted with id: {}", courseId);
    }

    public void evictCourseCacheByModule(Long moduleId) {
        Long courseId = moduleRepository.findCourseIdByModuleId(moduleId);
        evictCourseCache(courseId);
    }
}

