package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.CreateLessonDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateLessonDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateLessonInCourseDTO;
import ru.sweetbun.BecomeAnyone.entity.Lesson;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    private final ModelMapper modelMapper;

    private final ModuleService moduleService;

    @Autowired
    public LessonService(LessonRepository lessonRepository, ModelMapper modelMapper, @Lazy ModuleService moduleService) {
        this.lessonRepository = lessonRepository;
        this.modelMapper = modelMapper;
        this.moduleService = moduleService;
    }

    public Lesson createLesson(CreateLessonDTO lessonDTO, Long moduleId) {
        Module module = moduleService.getModuleById(moduleId);
        return lessonRepository.save(createLesson(lessonDTO, module));
    }

    public void createLessons(List<CreateLessonDTO> lessonDTOS, Module module) {
        List<Lesson> lessons = lessonDTOS.stream()
                .map(lessonDTO -> createLesson(lessonDTO, module))
                .toList();
        lessonRepository.saveAll(lessons);
    }

    private Lesson createLesson(CreateLessonDTO lessonDTO, Module module) {
        Lesson lesson = modelMapper.map(lessonDTO, Lesson.class);
        lesson.setModule(module);
        module.getLessons().add(lesson);
        return lesson;
    }

    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Lesson.class.getSimpleName(), id));
    }

    public List<Lesson> getAllLessonsByModule(Long moduleId) {
        return lessonRepository.findAllByModuleOrderByOrderNumAsc(moduleService.getModuleById(moduleId));
    }

    public Lesson updateLesson(UpdateLessonDTO updateLessonDTO, Long id) {
        Lesson lesson = getLessonById(id);
        modelMapper.map(updateLessonDTO, lesson);
        return lessonRepository.save(lesson);
    }

    public List<Lesson> updateLessons(List<UpdateLessonInCourseDTO> lessonDTOS, Module module) {
        Map<Long, Lesson> currentLessonsMap = module.getLessons().stream()
                .collect(Collectors.toMap(Lesson::getId, Function.identity()));
        List<Lesson> updatedLessons = new ArrayList<>();

        for (UpdateLessonInCourseDTO lessonDTO : lessonDTOS) {
            Long lessonDTOId = lessonDTO.getId();
            if (lessonDTOId != null && currentLessonsMap.containsKey(lessonDTOId)) {
                Lesson lesson = currentLessonsMap.get(lessonDTOId);
                currentLessonsMap.remove(lessonDTOId);
                modelMapper.map(lessonDTO, lesson);
                updatedLessons.add(lesson);
            } else {
                Lesson newLesson = modelMapper.map(lessonDTO, Lesson.class);
                newLesson.setModule(module);
                Lesson savedLesson = lessonRepository.save(newLesson);
                updatedLessons.add(savedLesson);
            }
        }
        lessonRepository.deleteAll(new ArrayList<>(currentLessonsMap.values()));
        return updatedLessons;
    }

    public String deleteLessonById(Long id) {
        Lesson lessonToDelete = getLessonById(id);
        int orderNum = lessonToDelete.getOrderNum();
        lessonRepository.deleteById(id);

        List<Lesson> lessonsToUpdate = lessonRepository.findByOrderNumGreaterThan(orderNum);
        lessonsToUpdate.forEach(lesson -> lesson.setOrderNum(lesson.getOrderNum() - 1));
        lessonRepository.saveAll(lessonsToUpdate);
        return "Lesson has been deleted with id: " + id;
    }
}
