package ru.sweetbun.becomeanyone.contract;

import ru.sweetbun.becomeanyone.dto.lesson.request.CreateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.response.LessonResponse;

import java.util.List;

public interface LessonService {

    LessonResponse createLesson(CreateLessonRequest lessonDTO, Long moduleId);
    List<LessonResponse> getAllLessonsByModule(Long moduleId);
    LessonResponse getLessonById(Long id);
    LessonResponse updateLesson(UpdateLessonRequest updateLessonRequest, Long id);
    long deleteLessonById(Long id);
}
