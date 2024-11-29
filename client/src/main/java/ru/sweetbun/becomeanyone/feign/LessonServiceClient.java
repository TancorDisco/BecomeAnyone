package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.lesson.request.CreateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.response.LessonResponse;

import java.util.List;

@FeignClient(name = "lessonService", url = "http://localhost:8080")
public interface LessonServiceClient {

    @PostMapping("/courses/{courseId}/modules/{moduleId}/lessons")
    LessonResponse createLesson(@RequestBody CreateLessonRequest lessonDTO, @PathVariable("moduleId") Long moduleId);

    @GetMapping("/courses/{courseId}/modules/{moduleId}/lessons")
    List<LessonResponse> getAllLessonsByModule(@PathVariable("moduleId") Long moduleId);

    @GetMapping("/courses/{courseId}/modules/{moduleId}/lessons/{id}")
    LessonResponse getLessonById(@PathVariable("id") Long id);

    @PatchMapping("/courses/{courseId}/modules/{moduleId}/lessons/{id}")
    LessonResponse updateLesson(@RequestBody UpdateLessonRequest updateLessonRequest, @PathVariable("id") Long id);

    @DeleteMapping("/courses/{courseId}/modules/{moduleId}/lessons/{id}")
    long deleteLessonById(@PathVariable("id") Long id);
}
