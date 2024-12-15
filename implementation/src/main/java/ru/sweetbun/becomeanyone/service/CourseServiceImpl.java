package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.contract.CourseService;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.course.CourseResponse;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.CourseRepository;
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

    private final NotificationService notificationService;

    @Override
    @Transactional
    public CourseResponse createCourse(CourseRequest<CreateModuleRequest> courseRequest) {
        User user = securityUtils.getCurrentUser();

        Course course = modelMapper.map(courseRequest, Course.class);
        course.setCreatedAt(now());
        course.setTeacher(user);

        Course savedCourse = courseRepository.save(course);
        moduleServiceImpl.createModules(courseRequest.getModules(), savedCourse);

        notificationService.notifyAboutNewCourse(course);
        return modelMapper.map(savedCourse, CourseResponse.class);
    }

    @Cacheable(value = "courses", key = "#id")
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
    public Page<CourseResponse> getAllCourses(Long teacherId, String q, int page, int pageSize) {
        Specification<Course> spec = Stream.of(
                ofNullable(teacherId).map(id -> CourseRepository.hasTeacher(userServiceImpl.fetchUserById(teacherId))),
                ofNullable(q).filter(title -> !title.isEmpty()).map(CourseRepository::hasTitle)
        )
                .flatMap(Optional::stream)
                .reduce(Specification::and)
                .orElse(Specification.where(null));

        Pageable pageable = PageRequest.of(page, pageSize);
        return courseRepository.findAll(spec, pageable)
                .map(course -> modelMapper.map(course, CourseResponse.class));
    }

    @CacheEvict(value = "courses", key = "#id")
    @Override
    @Transactional
    public CourseResponse updateCourseById(Long id, CourseRequest<UpdateModuleInCourseRequest> courseRequest) {
        Course course = fetchCourseById(id);
        modelMapper.map(courseRequest, course);
        course.setModules(moduleServiceImpl.updateModules(courseRequest.getModules(), course));
        course.getModules().forEach(module -> module.setCourse(course));
        course.setUpdatedAt(now());
        return modelMapper.map(course, CourseResponse.class);
    }

    @CacheEvict(value = "courses", key = "#id")
    @Override
    @Transactional
    public long deleteCourseById(Long id) {
        fetchCourseById(id);
        courseRepository.deleteById(id);
        return id;
    }

    public boolean isCourseOwner(Long courseId, String username) {
        Course course = fetchCourseById(courseId);
        return course.getTeacher().getUsername().equals(username);
    }
}
