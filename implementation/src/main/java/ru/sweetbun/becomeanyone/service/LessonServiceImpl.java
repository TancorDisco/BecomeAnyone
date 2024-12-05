package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.contract.FileDeletionEventPublisher;
import ru.sweetbun.becomeanyone.contract.LessonService;
import ru.sweetbun.becomeanyone.dto.lesson.request.CreateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonInCourseRequest;
import ru.sweetbun.becomeanyone.entity.AttachmentFile;
import ru.sweetbun.becomeanyone.entity.Content;
import ru.sweetbun.becomeanyone.entity.Lesson;
import ru.sweetbun.becomeanyone.entity.Module;
import ru.sweetbun.becomeanyone.dto.lesson.response.LessonResponse;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.FileRepository;
import ru.sweetbun.becomeanyone.repository.LessonRepository;
import ru.sweetbun.becomeanyone.repository.ModuleRepository;
import ru.sweetbun.becomeanyone.util.CacheServiceProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    private final ModelMapper modelMapper;
    @Lazy
    private final ModuleServiceImpl moduleServiceImpl;
    @Lazy
    private final ContentService contentService;

    private final CacheServiceProvider cacheServiceProvider;

    private final FileDeletionEventPublisher fileDeletionEventPublisher;
    @Lazy
    private final FileServiceImpl fileService;

    @Transactional
    public List<Lesson> updateLessons(List<UpdateLessonInCourseRequest> lessonDTOS, Module module) {
        Map<Long, Lesson> currentLessonsMap = module.getLessons().stream()
                .collect(Collectors.toMap(Lesson::getId, Function.identity()));

        List<Lesson> updatedLessons = mergeLessons(lessonDTOS, currentLessonsMap, module);

        if (!currentLessonsMap.isEmpty()) {
            currentLessonsMap.values().forEach(lesson -> deleteFilesInContent(lesson.getContent().getId()));
            lessonRepository.deleteAll(new ArrayList<>(currentLessonsMap.values()));
        }
        return updatedLessons;
    }

    private List<Lesson> mergeLessons(List<UpdateLessonInCourseRequest> lessonDTOS,
                                            Map<Long, Lesson> currentLessonsMap, Module module) {
        return lessonDTOS.stream().map(lessonDTO -> {
            Long lessonDTOId = lessonDTO.getId();
            Lesson lesson;

            if (lessonDTOId != null && currentLessonsMap.containsKey(lessonDTOId)) {
                lesson = currentLessonsMap.remove(lessonDTOId);
                modelMapper.map(lessonDTO, lesson);
            } else {
                lesson = modelMapper.map(lessonDTO, Lesson.class);
                lesson.setModule(module);
            }
            return lesson;
        }).toList();
    }

    @Override
    @Transactional
    public LessonResponse createLesson(CreateLessonRequest lessonDTO, Long moduleId) {
        cacheServiceProvider.evictCourseCacheByModuleId(moduleId);

        Module module = moduleServiceImpl.fetchModuleById(moduleId);
        Lesson lesson = lessonRepository.save(createLesson(lessonDTO, module));
        return modelMapper.map(lesson, LessonResponse.class);
    }

    @Transactional
    public void createLessons(List<CreateLessonRequest> lessonDTOS, Module module) {
        List<Lesson> lessons = lessonDTOS.stream()
                .map(lessonDTO -> createLesson(lessonDTO, module))
                .toList();
        if (!lessons.isEmpty()) lessonRepository.saveAll(lessons);
    }

    private Lesson createLesson(CreateLessonRequest lessonDTO, Module module) {
        Lesson lesson = modelMapper.map(lessonDTO, Lesson.class);
        lesson.setModule(module);
        module.getLessons().add(lesson);
        lesson.setContent(contentService.createContent(lesson));
        return lesson;
    }

    public Lesson fetchLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Lesson.class, id));
    }

    @Override
    public LessonResponse getLessonById(Long id) {
        Lesson lesson = fetchLessonById(id);
        return modelMapper.map(lesson, LessonResponse.class);
    }

    @Override
    public List<LessonResponse> getAllLessonsByModule(Long moduleId) {
        return lessonRepository.findAllByModuleOrderByOrderNumAsc(moduleServiceImpl.fetchModuleById(moduleId)).stream()
                .map(lesson -> modelMapper.map(lesson, LessonResponse.class))
                .toList();
    }

    @Override
    @Transactional
    public LessonResponse updateLesson(UpdateLessonRequest updateLessonRequest, Long id) {
        Lesson lesson = fetchLessonById(id);
        cacheServiceProvider.evictCourseCacheByLesson(lesson);

        lesson.setTitle(updateLessonRequest.title());
        if (updateLessonRequest.content() != null) {
            Content content = contentService.updateContent(updateLessonRequest.content(), lesson.getContent());
            lesson.setContent(content);
            content.setLesson(lesson);
        }
        Lesson savedLesson = lessonRepository.save(lesson);
        return modelMapper.map(savedLesson, LessonResponse.class);
    }

    @Override
    @Transactional
    public long deleteLessonById(Long id) {
        Lesson lessonToDelete = fetchLessonById(id);
        cacheServiceProvider.evictCourseCacheByLesson(lessonToDelete);
        deleteFilesInContent(lessonToDelete.getContent().getId());
        int orderNum = lessonToDelete.getOrderNum();
        lessonRepository.deleteById(id);

        List<Lesson> lessonsToUpdate = lessonRepository.findByOrderNumGreaterThan(orderNum);
        lessonsToUpdate.forEach(lesson -> lesson.setOrderNum(lesson.getOrderNum() - 1));
        lessonRepository.saveAll(lessonsToUpdate);
        return id;
    }

    private void deleteFilesInContent(Long contentId) {
        List<String> fileKeys = fileService.getFileKeysByContentId(contentId);
        fileKeys.forEach(fileDeletionEventPublisher::publishFileDeletionEvent);
    }
}
