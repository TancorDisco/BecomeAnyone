package ru.sweetbun.becomeanyone.metric;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Component
public class CourseMetrics {

    private final ConcurrentHashMap<Long, Counter> courseCounters = new ConcurrentHashMap<>();
    private final MeterRegistry meterRegistry;

    public void incrementCourseView(Long courseId) {
        courseCounters.computeIfAbsent(courseId, id -> Counter
                .builder("course.views")
                .description("Количество просмотров курса")
                .tag("courseId", id.toString())
                .register(meterRegistry))
                .increment();
    }
}
