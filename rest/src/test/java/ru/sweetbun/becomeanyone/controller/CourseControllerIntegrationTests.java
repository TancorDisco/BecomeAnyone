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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.aop.AccessControlAspect;
import ru.sweetbun.becomeanyone.config.AuthorizationFilter;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.CreateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonInCourseRequest;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.ModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.Lesson;
import ru.sweetbun.becomeanyone.entity.Module;
import ru.sweetbun.becomeanyone.entity.User;
import ru.sweetbun.becomeanyone.repository.CourseRepository;
import ru.sweetbun.becomeanyone.repository.LessonRepository;
import ru.sweetbun.becomeanyone.repository.ModuleRepository;
import ru.sweetbun.becomeanyone.service.NotificationService;
import ru.sweetbun.becomeanyone.service.UserServiceImpl;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static reactor.core.publisher.Mono.when;

@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CourseControllerIntegrationTests extends BaseIntegrationTests{

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private ModuleRepository moduleRepository;

    @Autowired
    private LessonRepository lessonRepository;

    @Autowired
    private ModelMapper modelMapper;

    @MockBean
    private SecurityUtils securityUtils;

    @MockBean
    private UserServiceImpl userServiceImpl;

    @MockBean
    private AuthorizationFilter filter;

    @MockBean
    private AccessControlAspect aspect;

    @MockBean
    private NotificationService notificationService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {

    }

    @ParameterizedTest
    @MethodSource("createCourse")
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

    static Stream<Arguments> createCourse() {
        return Stream.of(
                // Один модуль с уроками
                Arguments.of(
                        CourseRequest.builder()
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
                        CourseRequest.builder()
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
                        CourseRequest.builder()
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
                                                .lessons(List.of())
                                                .build()))
                                .build(),
                        2, 1, 0, "Module 1", "Module 2"),

                // Курс без модулей
                Arguments.of(
                        CourseRequest.builder()
                                .title("Course 4")
                                .modules(List.of())
                                .build(),
                        0, 0, 0, null, null),

                // Несколько модулей, оба с уроками
                Arguments.of(
                        CourseRequest.builder()
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

    @ParameterizedTest
    @MethodSource("updateCourse")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void shouldUpdateCourseWithDifferentScenarios(
            CourseRequest<UpdateModuleInCourseRequest> updateRequest,
            Consumer<Course> assertions) throws Exception {

        Course savedCourse = Course.builder()
                .title("Original Course Title")
                .description("Original Description")
                .modules(List.of(
                        Module.builder()
                                .title("Original Module 1")
                                .orderNum(1)
                                .lessons(List.of(
                                        Lesson.builder()
                                                .title("Original Lesson 1")
                                                .orderNum(1)
                                                .build()
                                ))
                                .build(),
                        Module.builder()
                                .title("Original Module 2")
                                .orderNum(2)
                                .lessons(Collections.emptyList())
                                .build()
                ))
                .createdAt(LocalDate.now())
                .build();

        courseRepository.save(savedCourse);

        // Act
        mockMvc.perform(patch("/courses/" + savedCourse.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk());

        // Assert
        Course updatedCourse = courseRepository.findById(savedCourse.getId()).orElseThrow();
        assertions.accept(updatedCourse);
    }

    static Stream<Arguments> updateCourse() {
        return Stream.of(
                // Сценарий 1: Все модули удалены
                Arguments.of(
                        CourseRequest.<UpdateModuleInCourseRequest>builder()
                                .title("Course With No Modules")
                                .description("Description After Deletion")
                                .modules(Collections.emptyList())
                                .build(),
                        (Consumer<Course>) course -> {
                            assertThat(course.getModules()).isEmpty();
                            assertThat(course.getTitle()).isEqualTo("Course With No Modules");
                            assertThat(course.getDescription()).isEqualTo("Description After Deletion");
                        }
                ),
                // Сценарий 2: Все уроки удалены из модулей
                Arguments.of(
                        CourseRequest.<UpdateModuleInCourseRequest>builder()
                                .title("Course With Empty Modules")
                                .description("Modules have no lessons")
                                .modules(List.of(
                                        UpdateModuleInCourseRequest.builder()
                                                .id(1L)
                                                .title("Updated Module 1")
                                                .orderNum(1)
                                                .lessons(Collections.emptyList())
                                                .build(),
                                        UpdateModuleInCourseRequest.builder()
                                                .id(2L)
                                                .title("Updated Module 2")
                                                .orderNum(2)
                                                .lessons(Collections.emptyList())
                                                .build()
                                ))
                                .build(),
                        (Consumer<Course>) course -> {
                            assertThat(course.getModules()).hasSize(2);
                            assertThat(course.getModules().get(0).getLessons()).isEmpty();
                            assertThat(course.getModules().get(1).getLessons()).isEmpty();
                        }
                ),
                // Сценарий 3: Добавлен новый модуль с уроками
                Arguments.of(
                        CourseRequest.<UpdateModuleInCourseRequest>builder()
                                .title("Course With New Module")
                                .description("Description After Adding Module")
                                .modules(List.of(
                                        UpdateModuleInCourseRequest.builder()
                                                .id(1L)
                                                .title("Existing Module")
                                                .orderNum(1)
                                                .lessons(List.of(
                                                        UpdateLessonInCourseRequest.builder()
                                                                .id(1L)
                                                                .title("Existing Lesson 1")
                                                                .orderNum(1)
                                                                .build()
                                                ))
                                                .build(),
                                        UpdateModuleInCourseRequest.builder()
                                                .title("New Module")
                                                .orderNum(2)
                                                .lessons(List.of(
                                                        UpdateLessonInCourseRequest.builder()
                                                                .title("New Lesson")
                                                                .orderNum(1)
                                                                .build()
                                                ))
                                                .build()
                                ))
                                .build(),
                        (Consumer<Course>) course -> {
                            assertThat(course.getModules()).hasSize(2);
                            assertThat(course.getModules().get(1).getTitle()).isEqualTo("New Module");
                            assertThat(course.getModules().get(1).getLessons()).hasSize(1);
                            assertThat(course.getModules().get(1).getLessons().get(0).getTitle()).isEqualTo("New Lesson");
                        }
                ),
                // Сценарий 4: Изменён существующий модуль и уроки
                Arguments.of(
                        CourseRequest.<UpdateModuleInCourseRequest>builder()
                                .title("Course With Updated Content")
                                .description("Description After Updates")
                                .modules(List.of(
                                        UpdateModuleInCourseRequest.builder()
                                                .id(1L)
                                                .title("Updated Module 1")
                                                .orderNum(1)
                                                .lessons(List.of(
                                                        UpdateLessonInCourseRequest.builder()
                                                                .id(1L)
                                                                .title("Updated Lesson 1")
                                                                .orderNum(1)
                                                                .build()
                                                ))
                                                .build()
                                ))
                                .build(),
                        (Consumer<Course>) course -> {
                            assertThat(course.getModules()).hasSize(1);
                            assertThat(course.getModules().get(0).getTitle()).isEqualTo("Updated Module 1");
                            assertThat(course.getModules().get(0).getLessons()).hasSize(1);
                            assertThat(course.getModules().get(0).getLessons().get(0).getTitle()).isEqualTo("Updated Lesson 1");
                        }
                ),
                // Сценарий 5
                Arguments.of(
                        CourseRequest.<UpdateModuleInCourseRequest>builder()
                                .title("Complex Update")
                                .description("Complex Scenario")
                                .modules(List.of(
                                        UpdateModuleInCourseRequest.builder()
                                                .id(1L)
                                                .title("Updated Module 1")
                                                .orderNum(1)
                                                .lessons(Collections.emptyList())
                                                .build(),
                                        UpdateModuleInCourseRequest.builder()
                                                .title("New Module Added")
                                                .orderNum(2)
                                                .lessons(List.of(
                                                        UpdateLessonInCourseRequest.builder()
                                                                .title("New Lesson in New Module")
                                                                .orderNum(1)
                                                                .build()
                                                ))
                                                .build()
                                ))
                                .build(),
                        (Consumer<Course>) course -> {
                            assertThat(course.getModules()).hasSize(2);
                            assertThat(course.getModules().get(0).getLessons()).isEmpty();
                            assertThat(course.getModules().get(1).getTitle()).isEqualTo("New Module Added");
                            assertThat(course.getModules().get(1).getLessons()).hasSize(1);
                        }
                )
        );
    }
}