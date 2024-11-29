package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.jpa.domain.Specification;
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.Module;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.repository.CourseRepository;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.course.CourseResponse;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTests {

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private SecurityUtils securityUtils;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private ModuleServiceImpl moduleServiceImpl;

    @Mock
    private UserServiceImpl userServiceImpl;

    @InjectMocks
    private CourseServiceImpl courseServiceImpl;

    private Course course;

    @BeforeEach
    void setUp() {
        courseServiceImpl = new CourseServiceImpl(courseRepository, modelMapper, moduleServiceImpl, securityUtils, userServiceImpl);

        course = new Course();
    }

    @Test
    void createCourse_ValidCourseDTO_SuccessfulCreation() {
        CourseRequest<CreateModuleRequest> courseDTO = new CourseRequest<>();
        courseDTO.setModules(List.of(new CreateModuleRequest()));
        User user = new User();

        when(securityUtils.getCurrentUser()).thenReturn(user);
        when(courseRepository.save(any(Course.class))).thenReturn(course);

        CourseResponse createdCourse = courseServiceImpl.createCourse(courseDTO);

        assertNotNull(createdCourse);
        verify(courseRepository, times(1)).save(any(Course.class));
        verify(moduleServiceImpl, times(1)).createModules(any(List.class), any(Course.class));
    }

    @Test
    void fetchCourseById_ValidId_CourseFound() {
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        Course foundCourse = courseServiceImpl.fetchCourseById(courseId);

        assertNotNull(foundCourse);
        assertEquals(course, foundCourse);
    }

    @Test
    void fetchCourseById_InvalidId_ThrowsResourceNotFoundException() {
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseServiceImpl.fetchCourseById(courseId));
    }

    @Test
    void deleteCourseById_ValidId_CourseDeleted() {
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        long deletedId = courseServiceImpl.deleteCourseById(courseId);

        assertEquals(courseId, deletedId);
        verify(courseRepository, times(1)).deleteById(courseId);
    }

    @Test
    void deleteCourseById_InvalidId_ThrowsResourceNotFoundException() {
        Long courseId = 1L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseServiceImpl.deleteCourseById(courseId));
        verify(courseRepository, never()).deleteById(anyLong());
    }

    @Test
    void getAllCourses_NoFilter_ReturnsAllCourses() {
        List<Course> courses = List.of(new Course(), new Course());
        when(courseRepository.findAll(any(Specification.class))).thenReturn(courses);

        List<CourseResponse> result = courseServiceImpl.getAllCourses(null, null);

        assertEquals(2, result.size());
        verify(courseRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void getAllCourses_WithTeacherId_ReturnsFilteredCourses() {
        Long teacherId = 1L;
        User user = new User();
        List<Course> courses = List.of(course);
        when(userServiceImpl.fetchUserById(teacherId)).thenReturn(user);
        when(courseRepository.findAll(any(Specification.class))).thenReturn(courses);

        List<CourseResponse> result = courseServiceImpl.getAllCourses(teacherId, null);

        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void getAllCourses_WithTitleQuery_ReturnsFilteredCourses() {
        String query = "Programming";
        List<Course> courses = List.of(course);
        when(courseRepository.findAll(any(Specification.class))).thenReturn(courses);

        List<CourseResponse> result = courseServiceImpl.getAllCourses(null, query);

        assertEquals(1, result.size());
        verify(courseRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void getAllCourses_NoCoursesFound_ReturnsEmptyList() {
        when(courseRepository.findAll(any(Specification.class))).thenReturn(List.of());

        List<CourseResponse> result = courseServiceImpl.getAllCourses(1L, "Not exist");

        assertTrue(result.isEmpty());
        verify(courseRepository, times(1)).findAll(any(Specification.class));
    }

    @Test
    void updateCourse_ValidCourseDTO_UpdatesCourseByIdSuccessfully() {
        Long courseId = 1L;
        CourseRequest<UpdateModuleInCourseRequest> courseDTO = new CourseRequest<>();
        courseDTO.setModules(List.of(new UpdateModuleInCourseRequest()));

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));
        when(moduleServiceImpl.updateModules(courseDTO.getModules(), course)).thenReturn(List.of(new Module()));

        CourseResponse updatedCourse = courseServiceImpl.updateCourseById(courseId, courseDTO);

        assertNotNull(updatedCourse);
        assertEquals(updatedCourse.getModules().size(), 1);
    }

    @Test
    void updateCourse_CourseByIdNotFound_ThrowsResourceNotFoundException() {
        Long courseId = 1L;
        CourseRequest<UpdateModuleInCourseRequest> courseDTO = new CourseRequest<>();
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> courseServiceImpl.updateCourseById(courseId, courseDTO));
        verify(courseRepository, never()).save(any(Course.class));
    }
}