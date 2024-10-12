package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.LessonDTO;
import ru.sweetbun.BecomeAnyone.entity.Lesson;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.LessonRepository;

import java.util.List;

@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public LessonService(LessonRepository lessonRepository, ModelMapper modelMapper) {
        this.lessonRepository = lessonRepository;
        this.modelMapper = modelMapper;
    }

    public Lesson createLesson(LessonDTO lessonDTO) {
        Lesson lesson = modelMapper.map(lessonDTO, Lesson.class);
        return lessonRepository.save(lesson);
    }

    public Lesson getLessonById(Long id) {
        return lessonRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Lesson.class.getSimpleName(), id));
    }

    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    public Lesson updateLesson(LessonDTO lessonDTO, Long id) {
        Lesson lesson = getLessonById(id);
        lesson = modelMapper.map(lessonDTO, Lesson.class);
        return lessonRepository.save(lesson);
    }

    public void deleteLessonById(Long id) {
        lessonRepository.deleteById(id);
    }
}
