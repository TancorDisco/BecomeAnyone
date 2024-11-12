package ru.sweetbun.become_anyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.become_anyone.DTO.CreateLessonDTO;
import ru.sweetbun.become_anyone.DTO.UpdateLessonDTO;
import ru.sweetbun.become_anyone.DTO.UpdateLessonInCourseDTO;
import ru.sweetbun.become_anyone.entity.Lesson;
import ru.sweetbun.become_anyone.entity.Module;
import ru.sweetbun.become_anyone.exception.ResourceNotFoundException;
import ru.sweetbun.become_anyone.repository.LessonRepository;

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

    @Transactional
    public List<Lesson> updateLessons(List<UpdateLessonInCourseDTO> lessonDTOS, Module module) {
        Map<Long, Lesson> currentLessonsMap = module.getLessons().stream()
                .collect(Collectors.toMap(Lesson::getId, Function.identity()));

        List<Lesson> updatedLessons = mergeLessons(lessonDTOS, currentLessonsMap, module);

        if (!currentLessonsMap.isEmpty())
            lessonRepository.deleteAll(new ArrayList<>(currentLessonsMap.values()));
        return updatedLessons;
    }

    private List<Lesson> mergeLessons(List<UpdateLessonInCourseDTO> lessonDTOS,
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
    public Lesson createLesson(CreateLessonDTO lessonDTO, Long moduleId) {
        Module module = moduleService.getModuleById(moduleId);
        return lessonRepository.save(createLesson(lessonDTO, module));
    }

    @Transactional
    public void createLessons(List<CreateLessonDTO> lessonDTOS, Module module) {
        List<Lesson> lessons = lessonDTOS.stream()
                .map(lessonDTO -> createLesson(lessonDTO, module))
                .toList();
        if (!lessons.isEmpty()) lessonRepository.saveAll(lessons);
    }

    private Lesson createLesson(CreateLessonDTO lessonDTO, Module module) {
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
    public Lesson updateLesson(UpdateLessonDTO updateLessonDTO, Long id) {
        Lesson lesson = getLessonById(id);
        modelMapper.map(updateLessonDTO, lesson);
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
