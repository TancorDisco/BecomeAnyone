package ru.sweetbun.BecomeAnyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.*;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.ModuleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    @Lazy
    private final LessonService lessonService;

    private final ModelMapper modelMapper;
    @Lazy
    private final CourseService courseService;

    public Module createModule(CreateModuleDTO moduleDTO, Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return createModule(moduleDTO, course);
    }

    @Transactional
    public void createModules(List<CreateModuleDTO> moduleDTOS, Course course) {
        for (CreateModuleDTO moduleDTO : moduleDTOS) {
            Module module = modelMapper.map(moduleDTO, Module.class);
            module.setCourse(course);
            course.getModules().add(module);
            moduleRepository.save(module);
            List<CreateLessonDTO> lessonDTOS = moduleDTO.lessons();
            if (!lessonDTOS.isEmpty()) {
                lessonService.createLessons(lessonDTOS, module);
            }
        }
    }

    @Transactional
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

    @Transactional
    public Module updateModule(UpdateModuleDTO updateModuleDTO, Long id) {
        Module module = getModuleById(id);
        modelMapper.map(updateModuleDTO, module);
        return moduleRepository.save(module);
    }

    @Transactional
    public List<Module> updateModules(List<UpdateModuleInCourseDTO> moduleDTOS, Course course) {
        Map<Long, Module> currentModulesMap = course.getModules().stream()
                .collect(Collectors.toMap(Module::getId, Function.identity()));

        List<Module> updatedModules = mergeModules(moduleDTOS, modelMapper, currentModulesMap, course,
                moduleRepository, lessonService);

        moduleRepository.deleteAll(new ArrayList<>(currentModulesMap.values()));
        return updatedModules;
    }

    @Transactional
    public static List<Module> mergeModules(List<UpdateModuleInCourseDTO> moduleDTOS, ModelMapper mapper,
                                            Map<Long, Module> currentModulesMap, Course course,
                                            ModuleRepository moduleRepository, LessonService lessonService) {
        return moduleDTOS.stream().map(moduleDTO -> {
            Module module;
            Long moduleDTOId = moduleDTO.id();

            if (moduleDTOId != null && currentModulesMap.containsKey(moduleDTOId)) {
                module = currentModulesMap.remove(moduleDTOId);
                mapper.map(moduleDTO, module);
            } else {
                module = mapper.map(moduleDTO, Module.class);
                module.setCourse(course);
            }
            module.setLessons(lessonService.updateLessons(moduleDTO.lessons(), module));
            return moduleRepository.save(module);
        }).collect(Collectors.toList());
    }


    @Transactional
    public long deleteModuleById(Long id) {
        Module moduleToDelete = getModuleById(id);
        int orderNum = moduleToDelete.getOrderNum();
        moduleRepository.deleteById(id);

        List<Module> modulesToUpdate = moduleRepository.findByOrderNumGreaterThan(orderNum);
        modulesToUpdate.forEach(module -> module.setOrderNum(module.getOrderNum() - 1));
        moduleRepository.saveAll(modulesToUpdate);
        return id;
    }
}
