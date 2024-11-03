package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.CourseDTO;
import ru.sweetbun.BecomeAnyone.DTO.CreateModuleDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateModuleInCourseDTO;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.User;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.CourseRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static java.util.Optional.ofNullable;

@Service
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    private final ModelMapper modelMapper;

    private final UserService userService;

    private final ModuleService moduleService;

    @Autowired
    public CourseService(CourseRepository courseRepository, ModelMapper modelMapper, UserService userService,
                         @Lazy ModuleService moduleService) {
        this.courseRepository = courseRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
        this.moduleService = moduleService;
    }

    public Course createCourse(CourseDTO<CreateModuleDTO> courseDTO) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);

        List<CreateModuleDTO> moduleDTOS = courseDTO.getModules();
        Course course = modelMapper.map(courseDTO, Course.class);
        course.setCreatedAt(now());
        course.setTeacher(user);

        Course savedCourse = courseRepository.save(course);
        if (!moduleDTOS.isEmpty()) {
            moduleService.createModules(moduleDTOS, savedCourse);
        }
        return savedCourse;
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Course.class.getSimpleName(), id));
    }

    public List<Course> getAllCourses(Long teacherId, String q) {
        Specification<Course> spec = Stream.of(
                ofNullable(teacherId).map(id -> CourseRepository.hasTeacher(userService.getUserById(id))),
                ofNullable(q).filter(title -> !title.isEmpty()).map(CourseRepository::hasTitle)
        )
                .flatMap(Optional::stream)
                .reduce(Specification::and)
                .orElse(null);
        return courseRepository.findAll(spec);
    }

    public Course updateCourse(CourseDTO<UpdateModuleInCourseDTO> courseDTO, Long id) {
        Course course = getCourseById(id);
        modelMapper.map(courseDTO, course);
        course.setModules(moduleService.updateModules(courseDTO.getModules(), course));
        course.setUpdatedAt(now());
        return courseRepository.save(course);
    }

    public void deleteCourseById(Long id) {
        getCourseById(id);
        courseRepository.deleteById(id);
    }
}
