package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.LessonDTO;
import ru.sweetbun.BecomeAnyone.entity.Lesson;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.LessonRepository;

import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    private final ModelMapper modelMapper;

    private final ModuleService moduleService;

    @Autowired
    public LessonService(LessonRepository lessonRepository, ModelMapper modelMapper, ModuleService moduleService) {
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

    public String deleteLessonById(Long id) {
        getLessonById(id);
        lessonRepository.deleteById(id);
        return "Lesson has been deleted with id: " + id;
    }
}
