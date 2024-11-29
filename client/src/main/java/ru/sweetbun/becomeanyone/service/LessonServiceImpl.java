package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.contract.LessonService;
import ru.sweetbun.becomeanyone.dto.lesson.request.CreateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.response.LessonResponse;
import ru.sweetbun.becomeanyone.feign.LessonServiceClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonServiceClient lessonServiceClient;

    @Override
    public LessonResponse createLesson(CreateLessonRequest lessonDTO, Long moduleId) {
        return lessonServiceClient.createLesson(lessonDTO, moduleId);
    }

    @Override
    public List<LessonResponse> getAllLessonsByModule(Long moduleId) {
        return lessonServiceClient.getAllLessonsByModule(moduleId);
    }

    @Override
    public LessonResponse getLessonById(Long id) {
        return lessonServiceClient.getLessonById(id);
    }

    @Override
    public LessonResponse updateLesson(UpdateLessonRequest updateLessonRequest, Long id) {
        return lessonServiceClient.updateLesson(updateLessonRequest, id);
    }

    @Override
    public long deleteLessonById(Long id) {
        return lessonServiceClient.deleteLessonById(id);
    }
}
