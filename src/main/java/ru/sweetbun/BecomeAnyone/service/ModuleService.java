package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.ModuleDTO;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.ModuleRepository;

import java.util.List;

@Service
public class ModuleService {

    private final ModuleRepository moduleRepository;

    private final ModelMapper modelMapper;

    private final CourseService courseService;

    @Autowired
    public ModuleService(ModuleRepository moduleRepository, ModelMapper modelMapper, CourseService courseService) {
        this.moduleRepository = moduleRepository;
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

    public String deleteModuleById(Long id) {
        getModuleById(id);
        moduleRepository.deleteById(id);
        return "Module has been deleted with id: " + id;
    }
}
