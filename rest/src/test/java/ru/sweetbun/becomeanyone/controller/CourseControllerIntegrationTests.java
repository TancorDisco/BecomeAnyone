package ru.sweetbun.becomeanyone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.config.AuthorizationFilter;
import ru.sweetbun.becomeanyone.contract.CourseService;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.CreateLessonRequest;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.ModuleRequest;
import ru.sweetbun.becomeanyone.entity.Module;
import ru.sweetbun.becomeanyone.entity.*;
import ru.sweetbun.becomeanyone.repository.*;
import ru.sweetbun.becomeanyone.service.ModuleServiceImpl;
import ru.sweetbun.becomeanyone.service.UserServiceImpl;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.lenient;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
public class CourseControllerIntegrationTests extends BaseIntegrationTests{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ModuleServiceImpl moduleServiceImpl;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @MockBean
    private SecurityUtils securityUtils;

    @MockBean
    private UserServiceImpl userServiceImpl;

    @MockBean
    private AuthorizationFilter filter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        Role role = new Role(1L, "ROLE_TEACHER");
        User mockUser = User.builder()
                .username("teacher").password("teacher").salt("salt").email("teacher@mail.ru").roles(Set.of(role)).build();
        roleRepository.save(role);
        userRepository.save(mockUser);
        lenient().when(securityUtils.getCurrentUser()).thenReturn(mockUser);
    }

    @Test
    @Transactional
    @WithMockUser(roles = "TEACHER")
    void shouldCreateCourseWithModulesAndLessons() throws Exception {
        // Arrange
        CreateLessonRequest lessonRequest = CreateLessonRequest.builder().title("Lesson 1").orderNum(1).build();
        CreateModuleRequest moduleRequest = CreateModuleRequest.builder()
                .title("Module 1").orderNum(1).lessons(List.of(lessonRequest)).build();
        CourseRequest<ModuleRequest> courseRequest = CourseRequest.builder()
                .title("Test Course").description("Test")
                .modules(List.of(moduleRequest)).build();

        // Act
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequest)))
                .andExpect(status().isOk());

        // Assert
        List<Course> courses = courseRepository.findAll();
        assertThat(courses).hasSize(1);

        Course savedCourse = courses.get(0);
        assertThat(savedCourse.getModules()).hasSize(1);
        Module savedModule = savedCourse.getModules().get(0);
        assertThat(savedModule.getLessons()).hasSize(1);
        Lesson savedLesson = savedModule.getLessons().get(0);

        assertThat(savedLesson.getTitle()).isEqualTo("Lesson 1");
        assertEquals(savedLesson.getOrderNum(), 1);
    }
}

/*

    @Test
    void shouldCreateCourse() throws Exception {
        String createCourseJson = """
        {
            "title": "Test Course",
            "description": "Integration testing course",
            "modules": []
        }
        """;

        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createCourseJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Course"));
    }

    @Test
    void shouldGetAllCourses() throws Exception {
        mockMvc.perform(get("/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void shouldGetCourseById() throws Exception {
        // Предполагаем, что в базе уже есть курс с ID = 1
        mockMvc.perform(get("/courses/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void shouldUpdateCourse() throws Exception {
        String updateCourseJson = """
        {
            "title": "Updated Test Course",
            "description": "Updated description",
            "modules": []
        }
        """;

        mockMvc.perform(patch("/courses/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateCourseJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Test Course"));
    }

    @Test
    void shouldDeleteCourse() throws Exception {
        // Предполагаем, что в базе уже есть курс с ID = 1
        mockMvc.perform(delete("/courses/1"))
                .andExpect(status().isOk());
    }
}
*/
