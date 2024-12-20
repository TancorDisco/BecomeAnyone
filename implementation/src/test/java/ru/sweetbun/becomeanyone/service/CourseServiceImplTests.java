package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private CourseServiceImpl courseServiceImpl;

    private Course course;

    @BeforeEach
    void setUp() {
        courseServiceImpl = new CourseServiceImpl(courseRepository, modelMapper, moduleServiceImpl, securityUtils,
                userServiceImpl, notificationService);

        course = new Course();
    }

    @Test
    void isCourseOwner_UserIsOwner_ReturnsTrue() {
        // Arrange
        Long courseId = 1L;
        String username = "teacher";
        course.setId(courseId);
        course.setTeacher(User.builder().username(username).build());
        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act
        boolean result = courseServiceImpl.isCourseOwner(courseId, username);

        // Assert
        assertTrue(result);
    }

    @Test
    void isCourseOwner_UserIsNotOwner_ReturnsFalse() {
        // Arrange
        Long courseId = 1L;
        String username = "user";
        User teacher = User.builder().username("teacher").build();
        course.setTeacher(teacher);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act
        boolean result = courseServiceImpl.isCourseOwner(courseId, username);

        // Assert
        assertFalse(result);
    }

    @Test
    void isCourseOwner_CourseNotFound_ThrowsException() {
        // Arrange
        Long courseId = 1L;
        String username = "teacher";

        when(courseRepository.findById(courseId)).thenThrow(ResourceNotFoundException.class);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                courseServiceImpl.isCourseOwner(courseId, username)
        );
    }

    @Test
    void getCourseById_ValidId_ReturnsMappedResponse() {
        // Arrange
        Long courseId = 1L;
        course = Course.builder().id(courseId).title("title").build();
        CourseResponse courseResponse = CourseResponse.builder().id(courseId).title("title").build();

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        // Act
        CourseResponse response = courseServiceImpl.getCourseById(courseId);

        // Assert
        assertNotNull(response);
        assertEquals(courseId, response.getId());
        assertEquals("title", response.getTitle());
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
        Page<Course> page = new PageImpl<>(courses);
        when(courseRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<CourseResponse> result = courseServiceImpl.getAllCourses(null, null, 0, 10);

        assertEquals(2, result.getContent().size());
        verify(courseRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllCourses_WithTeacherId_ReturnsFilteredCourses() {
        Long teacherId = 1L;
        User user = new User();
        List<Course> courses = List.of(course);
        Page<Course> page = new PageImpl<>(courses);
        when(userServiceImpl.fetchUserById(teacherId)).thenReturn(user);
        when(courseRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<CourseResponse> result = courseServiceImpl.getAllCourses(teacherId, null, 0, 10);

        assertEquals(1, result.getContent().size());
        verify(courseRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllCourses_WithTitleQuery_ReturnsFilteredCourses() {
        String query = "Programming";
        List<Course> courses = List.of(course);
        Page<Course> page = new PageImpl<>(courses);
        when(courseRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<CourseResponse> result = courseServiceImpl.getAllCourses(null, query, 0, 10);

        assertEquals(1, result.getContent().size());
        verify(courseRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void getAllCourses_NoCoursesFound_ReturnsEmptyList() {
        Page<Course> page = Page.empty();
        when(courseRepository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(page);

        Page<CourseResponse> result = courseServiceImpl.getAllCourses(1L, "Not exist", 0, 10);

        assertTrue(result.getContent().isEmpty());
        verify(courseRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
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