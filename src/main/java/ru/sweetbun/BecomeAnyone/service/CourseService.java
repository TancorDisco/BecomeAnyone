package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.CourseDTO;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.User;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.CourseRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    private final ModelMapper modelMapper;

    private final UserService userService;

    @Autowired
    public CourseService(CourseRepository courseRepository, ModelMapper modelMapper, UserService userService) {
        this.courseRepository = courseRepository;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    public Course createCourse(CourseDTO courseDTO) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userService.getUserByUsername(username);

        Course course = modelMapper.map(courseDTO, Course.class);
        course.setCreatedAt(LocalDate.now());
        course.setTeacher(user);

        return courseRepository.save(course);
    }

    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Course.class.getSimpleName(), id));
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course updateCourse(CourseDTO courseDTO, Long id) {
        Course course = getCourseById(id);
        course = modelMapper.map(courseDTO, Course.class);
        return courseRepository.save(course);
    }

    public void deleteCourseById(Long id) {
        courseRepository.deleteById(id);
    }
}
