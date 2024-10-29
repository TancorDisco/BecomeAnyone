package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.CreateLessonDTO;
import ru.sweetbun.BecomeAnyone.DTO.LessonDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateLessonDTO;
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

    public Lesson createLesson(LessonDTO lessonDTO, Long moduleId) {
        Lesson lesson = modelMapper.map(lessonDTO, Lesson.class);
        Module module = moduleService.getModuleById(moduleId);
        Lesson savedLesson = lessonRepository.save(lesson);
        savedLesson.setModule(module);
        module.getLessons().add(savedLesson);
        return lessonRepository.save(savedLesson);
    }

    public void createLessons(List<CreateLessonDTO> lessonDTOS, Module module) {
        for (CreateLessonDTO lessonDTO : lessonDTOS) {
            Lesson lesson = modelMapper.map(lessonDTO, Lesson.class);
            Lesson savedLesson = lessonRepository.save(lesson);
            savedLesson.setModule(module);
            module.getLessons().add(lesson);
            lessonRepository.save(savedLesson);
        }
    }

    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Lesson.class.getSimpleName(), id));
    }

    public List<Lesson> getAllLessonsByModule(Long moduleId) {
        return lessonRepository.findAllByModuleOrderByOrderNumAsc(moduleService.getModuleById(moduleId));
    }

    public Lesson updateLesson(LessonDTO lessonDTO, Long id) {
        Lesson lesson = getLessonById(id);
        modelMapper.map(lessonDTO, lesson);
        return lessonRepository.save(lesson);
    }

    public List<Lesson> updateLessons(List<UpdateLessonDTO> lessonDTOS, Module module) {
        Map<Long, Lesson> currentLessonsMap = module.getLessons().stream()
                .collect(Collectors.toMap(Lesson::getId, Function.identity()));

        List<Lesson> updatedLessons = new ArrayList<>();

        for (UpdateLessonDTO lessonDTO : lessonDTOS) {
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
        currentLessonsMap.values().forEach(lessonRepository::delete);
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
