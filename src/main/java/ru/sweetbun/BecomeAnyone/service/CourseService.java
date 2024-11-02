package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.CreateCourseDTO;
import ru.sweetbun.BecomeAnyone.DTO.CreateModuleDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateCourseDTO;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.User;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.CourseRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

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

    public Course createCourse(CreateCourseDTO createCourseDTO) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);

        List<CreateModuleDTO> moduleDTOS = createCourseDTO.getModules();
        Course course = modelMapper.map(createCourseDTO, Course.class);
        course.setCreatedAt(LocalDate.now());
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
                Optional.ofNullable(teacherId).map(id -> CourseRepository.hasTeacher(userService.getUserById(id))),
                Optional.ofNullable(q).filter(title -> !title.isEmpty()).map(CourseRepository::hasTitle)
        )
                .flatMap(Optional::stream)
                .reduce(Specification::and)
                .orElse(null);
        return courseRepository.findAll(spec);
    }

    public Course updateCourse(UpdateCourseDTO updateCourseDTO, Long id) {
        Course course = getCourseById(id);
        modelMapper.map(updateCourseDTO, course);
        course.setModules(moduleService.updateModules(updateCourseDTO.getModules(), course));
        return courseRepository.save(course);
    }

    public void deleteCourseById(Long id) {
        getCourseById(id);
        courseRepository.deleteById(id);
    }
}
