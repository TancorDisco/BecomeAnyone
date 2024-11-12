package ru.sweetbun.BecomeAnyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import ru.sweetbun.BecomeAnyone.DTO.CourseDTO;
import ru.sweetbun.BecomeAnyone.DTO.CreateModuleDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateModuleInCourseDTO;
import ru.sweetbun.BecomeAnyone.config.ModelMapperConfig;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.User;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.CourseRepository;
import ru.sweetbun.BecomeAnyone.util.SecurityUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceTests {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private SecurityUtils securityUtils;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private ModuleService moduleService;

    @Mock
    private UserService userService;

    @InjectMocks
    private CourseService courseService;

    @BeforeEach
    void setUp() {
        courseService = new CourseService(courseRepository, modelMapper, moduleService, securityUtils, userService);
    }

    @Test
    void createCourse_ValidCourseDTO_SuccessfulCreation() {
        CourseDTO<CreateModuleDTO> courseDTO = new CourseDTO<>();
        courseDTO.setModules(List.of(new CreateModuleDTO()));
        User user = new User();
        Course course = new Course();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        Course createdCourse = courseService.createCourse(courseDTO);

        assertNotNull(createdCourse);
        verify(courseRepository, times(1)).save(any(Course.class));
        verify(moduleService, times(1)).createModules(any(List.class), any(Course.class));
    }

    @Test
    void getCourseById_ValidId_CourseFound() {
        Long courseId = 1L;
        Course course = new Course();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        Course foundCourse = courseService.getCourseById(courseId);

        assertNotNull(foundCourse);
        assertEquals(course, foundCourse);
    }

    @Test
    void getCourseById_InvalidId_ThrowsResourceNotFoundException() {
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.getCourseById(courseId));
    }

    @Test
    void deleteCourseById_ValidId_CourseDeleted() {
        Long courseId = 1L;
        Course course = new Course();
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        long deletedId = courseService.deleteCourseById(courseId);

        assertEquals(courseId, deletedId);
        verify(courseRepository, times(1)).deleteById(courseId);
    }

    @Test
    void deleteCourseById_InvalidId_ThrowsResourceNotFoundException() {
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.deleteCourseById(courseId));
        verify(courseRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllCourses_NoFilter_ReturnsAllCourses() {
        List<Course> courses = List.of(new Course(), new Course());
        when(courseRepository.findAll(any(Specification.class))).thenReturn(courses);

        List<Course> result = courseService.getAllCourses(null, null);

        assertEquals(2, result.size());
        verify(courseRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void getAllCourses_WithTeacherId_ReturnsFilteredCourses() {
        Long teacherId = 1L;
        User user = new User();
        List<Course> courses = List.of(new Course());
        when(userService.getUserById(teacherId)).thenReturn(user);
        when(courseRepository.findAll(any(Specification.class))).thenReturn(courses);

        List<Course> result = courseService.getAllCourses(teacherId, null);

        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void getAllCourses_WithTitleQuery_ReturnsFilteredCourses() {
        String query = "Programming";
        List<Course> courses = List.of(new Course());
        when(courseRepository.findAll(any(Specification.class))).thenReturn(courses);

        List<Course> result = courseService.getAllCourses(null, query);

        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void getAllCourses_NoCoursesFound_ReturnsEmptyList() {
        when(courseRepository.findAll(any(Specification.class))).thenReturn(List.of());

        List<Course> result = courseService.getAllCourses(1L, "Not exist");

        assertTrue(result.isEmpty());
        verify(courseRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void updateCourse_ValidCourseDTO_UpdatesCourseSuccessfully() {
        Long courseId = 1L;
        Course existingCourse = new Course();
        CourseDTO<UpdateModuleInCourseDTO> courseDTO = new CourseDTO<>();
        courseDTO.setModules(List.of(new UpdateModuleInCourseDTO()));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(existingCourse));
        when(moduleService.updateModules(courseDTO.getModules(), existingCourse)).thenReturn(List.of());
        when(courseRepository.save(any(Course.class))).thenReturn(existingCourse);

        Course updatedCourse = courseService.updateCourse(courseDTO, courseId);

        assertNotNull(updatedCourse);
        verify(courseRepository, times(1)).save(existingCourse);
    }

    @Test
    void updateCourse_CourseNotFound_ThrowsResourceNotFoundException() {
        Long courseId = 1L;
        CourseDTO<UpdateModuleInCourseDTO> courseDTO = new CourseDTO<>();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseService.updateCourse(courseDTO, courseId));
        verify(courseRepository, never()).save(any(Course.class));
    }
}