package ru.sweetbun.become_anyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.become_anyone.DTO.CourseDTO;
import ru.sweetbun.become_anyone.DTO.CreateModuleDTO;
import ru.sweetbun.become_anyone.DTO.UpdateModuleInCourseDTO;
import ru.sweetbun.become_anyone.entity.Course;
import ru.sweetbun.become_anyone.entity.User;
import ru.sweetbun.become_anyone.exception.ResourceNotFoundException;
import ru.sweetbun.become_anyone.repository.CourseRepository;
import ru.sweetbun.become_anyone.util.SecurityUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static java.util.Optional.ofNullable;

@RequiredArgsConstructor
@Service
public class CourseService {

    private final CourseRepository courseRepository;

    private final ModelMapper modelMapper;
    @Lazy
    private final ModuleService moduleService;

    private final SecurityUtils securityUtils;

    private final UserService userService;

    @Transactional
    public Course createCourse(CourseDTO<CreateModuleDTO> courseDTO) {
        User user = securityUtils.getCurrentUser();

        Course course = modelMapper.map(courseDTO, Course.class);
        course.setCreatedAt(now());
        course.setTeacher(user);

        Course savedCourse = courseRepository.save(course);
        moduleService.createModules(courseDTO.getModules(), savedCourse);
        return savedCourse;
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Course.class, id));
    }

    public List<Course> getAllCourses(Long teacherId, String q) {
        Specification<Course> spec = Stream.of(
                ofNullable(teacherId).map(id -> CourseRepository.hasTeacher(userService.getUserById(teacherId))),
                ofNullable(q).filter(title -> !title.isEmpty()).map(CourseRepository::hasTitle)
        )
                .flatMap(Optional::stream)
                .reduce(Specification::and)
                .orElse(Specification.where(null));
        return courseRepository.findAll(spec);
    }

    @Transactional
    public Course updateCourse(CourseDTO<UpdateModuleInCourseDTO> courseDTO, Long id) {
        Course course = getCourseById(id);
        modelMapper.map(courseDTO, course);
        course.setModules(moduleService.updateModules(courseDTO.getModules(), course));
        course.setUpdatedAt(now());
        return courseRepository.save(course);
    }

    @Transactional
    public long deleteCourseById(Long id) {
        getCourseById(id);
        courseRepository.deleteById(id);
        return id;
    }
}
