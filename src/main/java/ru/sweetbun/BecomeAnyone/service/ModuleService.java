package ru.sweetbun.BecomeAnyone.service;

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

    @Autowired
    public ModuleService(ModuleRepository moduleRepository, @Lazy LessonService lessonService, ModelMapper modelMapper,
                         @Lazy CourseService courseService) {
        this.moduleRepository = moduleRepository;
        this.lessonService = lessonService;
        this.modelMapper = modelMapper;
        this.courseService = courseService;
    }

    public Module createModule(CreateModuleDTO moduleDTO, Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return createModule(moduleDTO, course);
    }

    public void createModules(List<CreateModuleDTO> moduleDTOS, Course course) {
        for (CreateModuleDTO moduleDTO : moduleDTOS) {
            Module module = modelMapper.map(moduleDTO, Module.class);
            module.setCourse(course);
            course.getModules().add(module);
            moduleRepository.save(module);
            List<CreateLessonDTO> lessonDTOS = moduleDTO.getLessons();
            if (!lessonDTOS.isEmpty()) {
                lessonService.createLessons(lessonDTOS, module);
            }
        }
    }

    private Module createModule(CreateModuleDTO moduleDTO, Course course) {
        Module module = modelMapper.map(moduleDTO, Module.class);
        module.setCourse(course);
        course.getModules().add(module);
        return moduleRepository.save(module);
    }

    public Module getModuleById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Module.class, id));
    }

    public List<Module> getAllModulesByCourse(Long courseId) {
        return moduleRepository.findAllByCourseOrderByOrderNumAsc(courseService.getCourseById(courseId));
    }

    public Module updateModule(UpdateModuleDTO updateModuleDTO, Long id) {
        Module module = getModuleById(id);
        modelMapper.map(updateModuleDTO, module);
        return moduleRepository.save(module);
    }

    public List<Module> updateModules(List<UpdateModuleInCourseDTO> moduleDTOS, Course course) {
        Map<Long, Module> currentModulesMap = course.getModules().stream()
                .collect(Collectors.toMap(Module::getId, Function.identity()));
        List<Module> updatedModules = new ArrayList<>();

        for (UpdateModuleInCourseDTO moduleDTO : moduleDTOS) {
            Long moduleDTOId = moduleDTO.getId();
            if (moduleDTOId != null && currentModulesMap.containsKey(moduleDTOId)) {
                Module module = currentModulesMap.get(moduleDTOId);
                currentModulesMap.remove(moduleDTOId);
                List<UpdateLessonInCourseDTO> lessonDTOS = moduleDTO.getLessons();
                modelMapper.map(moduleDTO, module);
                updatedModules.add(updateLessonsForModule(lessonDTOS, module));
            } else {
                List<UpdateLessonInCourseDTO> lessonDTOS = moduleDTO.getLessons();
                Module newModule = modelMapper.map(moduleDTO, Module.class);
                newModule.setCourse(course);
                Module savedModule = moduleRepository.save(newModule);
                updatedModules.add(updateLessonsForModule(lessonDTOS, savedModule));
            }
        }
        moduleRepository.deleteAll(new ArrayList<>(currentModulesMap.values()));
        return updatedModules;
    }

    private Module updateLessonsForModule(List<UpdateLessonInCourseDTO> lessonDTOS, Module module) {
        module.setLessons(lessonService.updateLessons(lessonDTOS, module));
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
