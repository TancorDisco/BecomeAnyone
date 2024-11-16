package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.contract.ModuleService;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.Module;
import ru.sweetbun.becomeanyone.dto.module.response.ModuleResponse;
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
public class ModuleServiceImpl implements ModuleService {

    private final ModuleRepository moduleRepository;
    @Lazy
    private final LessonServiceImpl lessonServiceImpl;

    private final ModelMapper modelMapper;
    @Lazy
    private final CourseServiceImpl courseServiceImpl;

    @Override
    @Transactional
    public ModuleResponse createModule(CreateModuleRequest moduleDTO, Long courseId) {
        Course course = courseServiceImpl.fetchCourseById(courseId);
        Module module = moduleRepository.save(createModule(moduleDTO, course));
        return modelMapper.map(module, ModuleResponse.class);
    }

    @Transactional
    public void createModules(List<CreateModuleRequest> moduleDTOS, Course course) {
        for (CreateModuleRequest moduleDTO : moduleDTOS) {
            Module module = moduleRepository.save(createModule(moduleDTO, course));
            lessonServiceImpl.createLessons(moduleDTO.getLessons(), module);
        }
    }

    private Module createModule(CreateModuleRequest moduleDTO, Course course) {
        Module module = modelMapper.map(moduleDTO, Module.class);
        module.setCourse(course);
        course.getModules().add(module);
        return module;
    }

    @Override
    public ModuleResponse getModuleById(Long id) {
        Module module = fetchModuleById(id);
        return modelMapper.map(module, ModuleResponse.class);
    }

    public Module fetchModuleById(Long id) {
        return moduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Module.class, id));
    }

    @Override
    public List<ModuleResponse> getAllModulesByCourse(Long courseId) {
        return moduleRepository.findAllByCourseOrderByOrderNumAsc(courseServiceImpl.fetchCourseById(courseId)).stream()
                .map(module -> modelMapper.map(module, ModuleResponse.class))
                .toList();
    }

    @Override
    @Transactional
    public ModuleResponse updateModule(UpdateModuleRequest updateModuleDTO, Long id) {
        Module module = fetchModuleById(id);
        modelMapper.map(updateModuleDTO, module);
        Module savedModule = moduleRepository.save(module);
        return modelMapper.map(savedModule, ModuleResponse.class);
    }

    @Transactional
    public List<Module> updateModules(List<UpdateModuleInCourseRequest> moduleDTOS, Course course) {
        Map<Long, Module> currentModulesMap = course.getModules().stream()
                .collect(Collectors.toMap(Module::getId, Function.identity()));

        List<Module> updatedModules = mergeModules(moduleDTOS, currentModulesMap, course);
        if (!currentModulesMap.isEmpty())
            moduleRepository.deleteAll(new ArrayList<>(currentModulesMap.values()));
        return updatedModules;
    }

    private List<Module> mergeModules(List<UpdateModuleInCourseRequest> moduleDTOS,
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
            module.setLessons(lessonServiceImpl.updateLessons(moduleDTO.getLessons(), module));
            return moduleRepository.save(module);
        }).toList();
    }

    @Override
    @Transactional
    public long deleteModuleById(Long id) {
        Module moduleToDelete = fetchModuleById(id);
        int orderNum = moduleToDelete.getOrderNum();
        moduleRepository.deleteById(id);

        List<Module> modulesToUpdate = moduleRepository.findByOrderNumGreaterThan(orderNum);
        modulesToUpdate.forEach(module -> module.setOrderNum(module.getOrderNum() - 1));
        moduleRepository.saveAll(modulesToUpdate);
        return id;
    }
}
