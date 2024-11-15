package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.dto.lesson.request.CreateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonInCourseRequest;
import ru.sweetbun.becomeanyone.domain.entity.Content;
import ru.sweetbun.becomeanyone.domain.entity.Lesson;
import ru.sweetbun.becomeanyone.domain.entity.Module;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.domain.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    private final ModelMapper modelMapper;
    @Lazy
    private final ModuleService moduleService;

    private final ContentService contentService;

    @Transactional
    public List<Lesson> updateLessons(List<UpdateLessonInCourseRequest> lessonDTOS, Module module) {
        Map<Long, Lesson> currentLessonsMap = module.getLessons().stream()
                .collect(Collectors.toMap(Lesson::getId, Function.identity()));

        List<Lesson> updatedLessons = mergeLessons(lessonDTOS, currentLessonsMap, module);

        if (!currentLessonsMap.isEmpty())
            lessonRepository.deleteAll(new ArrayList<>(currentLessonsMap.values()));
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

    @Transactional
    public Lesson createLesson(CreateLessonRequest lessonDTO, Long moduleId) {
        Module module = moduleService.getModuleById(moduleId);
        return lessonRepository.save(createLesson(lessonDTO, module));
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
        return lesson;
    }

    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Lesson.class, id));
    }

    public List<Lesson> getAllLessonsByModule(Long moduleId) {
        return lessonRepository.findAllByModuleOrderByOrderNumAsc(moduleService.getModuleById(moduleId));
    }

    @Transactional
    public Lesson updateLesson(UpdateLessonRequest updateLessonRequest, Long id) {
        Lesson lesson = getLessonById(id);
        lesson.setTitle(updateLessonRequest.title());
        Content content = contentService.updateContent(updateLessonRequest.content(), lesson.getContent());
        lesson.setContent(content);
        content.setLesson(lesson);
        return lessonRepository.save(lesson);
    }

    @Transactional
    public long deleteLessonById(Long id) {
        Lesson lessonToDelete = getLessonById(id);
        int orderNum = lessonToDelete.getOrderNum();
        lessonRepository.deleteById(id);

        List<Lesson> lessonsToUpdate = lessonRepository.findByOrderNumGreaterThan(orderNum);
        lessonsToUpdate.forEach(lesson -> lesson.setOrderNum(lesson.getOrderNum() - 1));
        lessonRepository.saveAll(lessonsToUpdate);
        return id;
    }


}
