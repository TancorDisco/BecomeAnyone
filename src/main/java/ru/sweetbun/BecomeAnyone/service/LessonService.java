package ru.sweetbun.BecomeAnyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.CreateLessonDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateLessonDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateLessonInCourseDTO;
import ru.sweetbun.BecomeAnyone.entity.Lesson;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.mapper.UpdateLessonInCourseMapper;
import ru.sweetbun.BecomeAnyone.repository.LessonRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class LessonService {

    private final LessonRepository lessonRepository;

    private final ModelMapper modelMapper;
    @Lazy
    private final ModuleService moduleService;

    private final UpdateLessonInCourseMapper updateLessonInCourseMapper = UpdateLessonInCourseMapper.INSTANCE;

    @Transactional
    public List<Lesson> updateLessons(List<UpdateLessonInCourseDTO> lessonDTOS, Module module) {
        Map<Long, Lesson> currentLessonsMap = module.getLessons().stream()
                .collect(Collectors.toMap(Lesson::getId, Function.identity()));

        List<Lesson> updatedLessons = mergeLessons(lessonDTOS, updateLessonInCourseMapper, currentLessonsMap, module);

        lessonRepository.deleteAll(new ArrayList<>(currentLessonsMap.values()));
        return updatedLessons;
    }

    public static List<Lesson> mergeLessons(List<UpdateLessonInCourseDTO> lessonDTOS, UpdateLessonInCourseMapper mapper,
                                            Map<Long, Lesson> currentLessonsMap, Module module) {
        List<Lesson> updatedLessons = new ArrayList<>();

        for (UpdateLessonInCourseDTO lessonDTO : lessonDTOS) {
            Long lessonDTOId = lessonDTO.id();
            if (lessonDTOId != null && currentLessonsMap.containsKey(lessonDTOId)) {
                Lesson lesson = currentLessonsMap.get(lessonDTOId);
                currentLessonsMap.remove(lessonDTOId);
                lesson = mapper.toLesson(lessonDTO);
                updatedLessons.add(lesson);
            } else {
                Lesson newLesson = mapper.toLesson(lessonDTO);
                newLesson.setModule(module);
                updatedLessons.add(newLesson);
            }
        }
        return updatedLessons;
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

    /*@Transactional
    public List<Lesson> updateLessons(List<UpdateLessonInCourseDTO> lessonDTOS, Module module) {
        Map<Long, Lesson> currentLessonsMap = module.getLessons().stream()
                .collect(Collectors.toMap(Lesson::getId, Function.identity()));
        List<Lesson> updatedLessons = new ArrayList<>();

        for (UpdateLessonInCourseDTO lessonDTO : lessonDTOS) {
            Long lessonDTOId = lessonDTO.id();
            if (lessonDTOId != null && currentLessonsMap.containsKey(lessonDTOId)) {
                Lesson lesson = currentLessonsMap.get(lessonDTOId);
                currentLessonsMap.remove(lessonDTOId);
                lesson = updateLessonInCourseMapper.toLesson(lessonDTO);
                //modelMapper.map(lessonDTO, lesson);
                updatedLessons.add(lesson);
            } else {
                Lesson newLesson = updateLessonInCourseMapper.toLesson(lessonDTO);
                newLesson.setModule(module);
                Lesson savedLesson = lessonRepository.save(newLesson);
                updatedLessons.add(savedLesson);
            }
        }
        lessonRepository.deleteAll(new ArrayList<>(currentLessonsMap.values()));
        return updatedLessons;
    }*/

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
