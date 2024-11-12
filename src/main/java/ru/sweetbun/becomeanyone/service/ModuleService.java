package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.DTO.*;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.Module;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.ModuleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class ModuleService {

    private final ModuleRepository moduleRepository;
    @Lazy
    private final LessonService lessonService;

    private final ModelMapper modelMapper;
    @Lazy
    private final CourseService courseService;

    @Transactional
    public Module createModule(CreateModuleDTO moduleDTO, Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return moduleRepository.save(createModule(moduleDTO, course));
    }

    @Transactional
    public void createModules(List<CreateModuleDTO> moduleDTOS, Course course) {
        for (CreateModuleDTO moduleDTO : moduleDTOS) {
            Module module = moduleRepository.save(createModule(moduleDTO, course));
            lessonService.createLessons(moduleDTO.getLessons(), module);
        }
    }

    private Module createModule(CreateModuleDTO moduleDTO, Course course) {
        Module module = modelMapper.map(moduleDTO, Module.class);
        module.setCourse(course);
        course.getModules().add(module);
        return module;
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

        List<Module> updatedModules = mergeModules(moduleDTOS, currentModulesMap, course);
        if (!currentModulesMap.isEmpty())
            moduleRepository.deleteAll(new ArrayList<>(currentModulesMap.values()));
        return updatedModules;
    }

    private List<Module> mergeModules(List<UpdateModuleInCourseDTO> moduleDTOS,
                                            Map<Long, Module> currentModulesMap, Course course) {
        return moduleDTOS.stream().map(moduleDTO -> {
            Module module;
            Long moduleDTOId = moduleDTO.getId();

            if (moduleDTOId != null && currentModulesMap.containsKey(moduleDTOId)) {
                module = currentModulesMap.remove(moduleDTOId);
                modelMapper.map(moduleDTO, module);
            } else {
                module = modelMapper.map(moduleDTO, Module.class);
                module.setCourse(course);
            }
            module.setLessons(lessonService.updateLessons(moduleDTO.getLessons(), module));
            return moduleRepository.save(module);
        }).toList();
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
