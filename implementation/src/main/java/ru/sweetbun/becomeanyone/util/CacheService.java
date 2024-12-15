package ru.sweetbun.becomeanyone.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CacheService {

    @CacheEvict(value = "courses", key = "#courseId")
    public void evictCourseCache(Long courseId) {
        log.info("Cache Course deleted with id: {}", courseId);
    }
}

