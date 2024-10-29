package ru.sweetbun.BecomeAnyone.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.*;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.Lesson;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.ModuleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
public class ModuleService {

    private final ModuleRepository moduleRepository;

    private final LessonService lessonService;

    private final ModelMapper modelMapper;

    private final CourseService courseService;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ModuleService(ModuleRepository moduleRepository, @Lazy LessonService lessonService, ModelMapper modelMapper,
                         @Lazy CourseService courseService) {
        this.moduleRepository = moduleRepository;
        this.lessonService = lessonService;
        this.modelMapper = modelMapper;
        this.courseService = courseService;
    }

    public Module createModule(ModuleDTO moduleDTO, Long courseId) {
        Module module = modelMapper.map(moduleDTO, Module.class);
        Course course = courseService.getCourseById(courseId);
        Module savedModule = moduleRepository.save(module);
        savedModule.setCourse(course);
        course.getModules().add(savedModule);
        return moduleRepository.save(savedModule);
    }

    public void createModules(List<CreateModuleDTO> moduleDTOS, Course course) {
        for (CreateModuleDTO moduleDTO : moduleDTOS) {
            Module module = modelMapper.map(moduleDTO, Module.class);
            List<CreateLessonDTO> lessonDTOS = moduleDTO.getLessons();
            module.setLessons(new ArrayList<>());
            Module savedModule = moduleRepository.save(module);

            savedModule.setCourse(course);
            course.getModules().add(savedModule);
            moduleRepository.save(savedModule);
            if (!lessonDTOS.isEmpty()) {
                lessonService.createLessons(lessonDTOS, savedModule);
            }
        }
    }

    public Module getModuleById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Module.class.getSimpleName(), id));
    }

    public List<Module> getAllModulesByCourse(Long courseId) {
        return moduleRepository.findAllByCourseOrderByOrderNumAsc(courseService.getCourseById(courseId));
    }

    public Module updateModule(ModuleDTO moduleDTO, Long id) {
        Module module = getModuleById(id);
        modelMapper.map(moduleDTO, module);
        return moduleRepository.save(module);
    }

    public List<Module> updateModules(List<UpdateModuleDTO> moduleDTOS, Course course) {
        Map<Long, Module> currentModulesMap = course.getModules().stream()
                .collect(Collectors.toMap(Module::getId, Function.identity()));
        List<Module> updatedModules = new ArrayList<>();

        for (UpdateModuleDTO moduleDTO : moduleDTOS) {
            Long moduleDTOId = moduleDTO.getId();
            if (moduleDTOId != null && currentModulesMap.containsKey(moduleDTOId)) {
                Module module = currentModulesMap.get(moduleDTOId);
                currentModulesMap.remove(moduleDTOId);
                List<UpdateLessonDTO> lessonDTOS = moduleDTO.getLessons();
                moduleDTO.setLessons(new ArrayList<>());
                modelMapper.map(moduleDTO, module);
                updatedModules.add(updateLessonsForModule(lessonDTOS, module));
            } else {
                List<UpdateLessonDTO> lessonDTOS = moduleDTO.getLessons();
                moduleDTO.setLessons(new ArrayList<>());
                Module newModule = modelMapper.map(moduleDTO, Module.class);
                newModule.setCourse(course);
                Module savedModule = moduleRepository.save(newModule);
                updatedModules.add(updateLessonsForModule(lessonDTOS, savedModule));
            }
        }
        currentModulesMap.values().forEach(moduleRepository::delete);
        return updatedModules;
    }

    private Module updateLessonsForModule(List<UpdateLessonDTO> lessonDTOS, Module module) {
        List<Lesson> lessons = lessonService.updateLessons(lessonDTOS, module);
        module.setLessons(lessons);
        return moduleRepository.save(module);
    }

    public String deleteModuleById(Long id) {
        Module moduleToDelete = getModuleById(id);
        int orderNum = moduleToDelete.getOrderNum();
        moduleRepository.deleteById(id);

        List<Module> modulesToUpdate = moduleRepository.findByOrderNumGreaterThan(orderNum);
        modulesToUpdate.forEach(module -> module.setOrderNum(module.getOrderNum() - 1));
        moduleRepository.saveAll(modulesToUpdate);
        return "Module has been deleted with id: " + id;
    }
}
