package ru.sweetbun.becomeanyone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.Module;
import ru.sweetbun.becomeanyone.repository.CourseRepository;
import ru.sweetbun.becomeanyone.repository.LessonRepository;
import ru.sweetbun.becomeanyone.repository.ModuleRepository;
import ru.sweetbun.becomeanyone.repository.UserRepository;
import ru.sweetbun.becomeanyone.service.ModuleServiceImpl;
import ru.sweetbun.becomeanyone.service.UserServiceImpl;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
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

    @MockBean
    private SecurityUtils securityUtils;

    @MockBean
    private UserServiceImpl userServiceImpl;

    @MockBean
    private AuthorizationFilter filter;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

    }

    @ParameterizedTest
    @MethodSource("courseTestData")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void shouldCreateCourseWithVariousModuleAndLessonCombinations(
            CourseRequest<ModuleRequest> courseRequest,
            int expectedModulesCount,
            int expectedModule1LessonsCount,
            int expectedModule2LessonsCount,
            String expectedModule1Title,
            String expectedModule2Title) throws Exception {

        // Act
        mockMvc.perform(post("/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseRequest)))
                .andExpect(status().isOk());

        // Assert
        List<Course> courses = courseRepository.findAll();
        assertThat(courses).hasSize(1);

        Course savedCourse = courses.get(0);

        // Проверка на количество модулей
        assertThat(savedCourse.getModules()).hasSize(expectedModulesCount);

        if (expectedModulesCount > 0) {
            Module module1 = savedCourse.getModules().get(0);
            assertThat(module1.getLessons()).hasSize(expectedModule1LessonsCount);
            assertThat(module1.getTitle()).isEqualTo(expectedModule1Title);

            // Если есть второй модуль
            if (expectedModulesCount > 1) {
                Module module2 = savedCourse.getModules().get(1);
                assertThat(module2.getLessons()).hasSize(expectedModule2LessonsCount);
                assertThat(module2.getTitle()).isEqualTo(expectedModule2Title);
            }
        }
    }

    static Stream<Arguments> courseTestData() {
        return Stream.of(
                // Один модуль с уроками
                Arguments.of(
                        CourseRequest.<ModuleRequest>builder()
                                .title("Course 1")
                                .modules(List.of(
                                        CreateModuleRequest.builder()
                                                .title("Module 1")
                                                .orderNum(1)
                                                .lessons(List.of(CreateLessonRequest.builder().title("Lesson 1").orderNum(1).build()))
                                                .build()))
                                .build(),
                        1, 1, 0, "Module 1", null),

                // Один модуль без уроков
                Arguments.of(
                        CourseRequest.<ModuleRequest>builder()
                                .title("Course 2")
                                .modules(List.of(
                                        CreateModuleRequest.builder()
                                                .title("Module 1")
                                                .orderNum(1)
                                                .lessons(List.of())  // Без уроков
                                                .build()))
                                .build(),
                        1, 0, 0, "Module 1", null),

                // Несколько модулей: один с уроками, второй без
                Arguments.of(
                        CourseRequest.<ModuleRequest>builder()
                                .title("Course 3")
                                .modules(List.of(
                                        CreateModuleRequest.builder()
                                                .title("Module 1")
                                                .orderNum(1)
                                                .lessons(List.of(CreateLessonRequest.builder().title("Lesson 1").orderNum(1).build()))
                                                .build(),
                                        CreateModuleRequest.builder()
                                                .title("Module 2")
                                                .orderNum(2)
                                                .lessons(List.of())  // Без уроков
                                                .build()))
                                .build(),
                        2, 1, 0, "Module 1", "Module 2"),

                // Курс без модулей
                Arguments.of(
                        CourseRequest.<ModuleRequest>builder()
                                .title("Course 4")
                                .modules(List.of())  // Без модулей
                                .build(),
                        0, 0, 0, null, null),

                // Несколько модулей, оба с уроками
                Arguments.of(
                        CourseRequest.<ModuleRequest>builder()
                                .title("Course 5")
                                .modules(List.of(
                                        CreateModuleRequest.builder()
                                                .title("Module 1")
                                                .orderNum(1)
                                                .lessons(List.of(CreateLessonRequest.builder().title("Lesson 1").orderNum(1).build()))
                                                .build(),
                                        CreateModuleRequest.builder()
                                                .title("Module 2")
                                                .orderNum(2)
                                                .lessons(List.of(CreateLessonRequest.builder().title("Lesson 2").orderNum(1).build()))
                                                .build()))
                                .build(),
                        2, 1, 1, "Module 1", "Module 2")
        );
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
