package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.contract.CourseService;
import ru.sweetbun.becomeanyone.domain.entity.Course;
import ru.sweetbun.becomeanyone.domain.entity.User;
import ru.sweetbun.becomeanyone.domain.repository.CourseRepository;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.course.CourseResponse;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.time.LocalDate.now;
import static java.util.Optional.ofNullable;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    private final ModelMapper modelMapper;
    @Lazy
    private final ModuleServiceImpl moduleServiceImpl;

    private final SecurityUtils securityUtils;

    private final UserServiceImpl userServiceImpl;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest<CreateModuleRequest> courseRequest) {
        User user = securityUtils.getCurrentUser();

        Course course = modelMapper.map(courseRequest, Course.class);
        course.setCreatedAt(now());
        course.setTeacher(user);

        Course savedCourse = courseRepository.save(course);
        moduleServiceImpl.createModules(courseRequest.getModules(), savedCourse);
        return modelMapper.map(savedCourse, CourseResponse.class);
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        Course course = fetchCourseById(id);
        return modelMapper.map(course, CourseResponse.class);
    }

    public Course fetchCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Course.class, id));
    }

    @Override
    public List<CourseResponse> getAllCourses(Long teacherId, String q) {
        Specification<Course> spec = Stream.of(
                ofNullable(teacherId).map(id -> CourseRepository.hasTeacher(userServiceImpl.fetchUserById(teacherId))),
                ofNullable(q).filter(title -> !title.isEmpty()).map(CourseRepository::hasTitle)
        )
                .flatMap(Optional::stream)
                .reduce(Specification::and)
                .orElse(Specification.where(null));
        return courseRepository.findAll(spec).stream()
                .map(course -> modelMapper.map(course, CourseResponse.class))
                .toList();
    }

    @Override
    @Transactional
    public CourseResponse updateCourseById(Long id, CourseRequest<UpdateModuleInCourseRequest> courseRequest) {
        Course course = fetchCourseById(id);
        modelMapper.map(courseRequest, course);
        course.setModules(moduleServiceImpl.updateModules(courseRequest.getModules(), course));
        course.setUpdatedAt(now());
        Course savedCourse = courseRepository.save(course);
        return modelMapper.map(savedCourse, CourseResponse.class);
    }

    @Override
    @Transactional
    public long deleteCourseById(Long id) {
        fetchCourseById(id);
        courseRepository.deleteById(id);
        return id;
    }
}
