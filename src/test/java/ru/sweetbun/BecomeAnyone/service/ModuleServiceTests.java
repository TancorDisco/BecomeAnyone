package ru.sweetbun.BecomeAnyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.BecomeAnyone.DTO.UpdateModuleInCourseDTO;
import ru.sweetbun.BecomeAnyone.config.ModelMapperConfig;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.Lesson;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.repository.ModuleRepository;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ModuleServiceTests {

    private Course course;
    private Map<Long, Module> currentModulesMap;
    private ModelMapper modelMapper;

    @Mock
    private LessonService lessonService;

    @Mock
    private ModuleRepository moduleRepository;

    @BeforeEach
    public void setUp() {
        course = new Course();
        currentModulesMap = new HashMap<>();
        modelMapper = ModelMapperConfig.createConfiguredModelMapper();

        Module module1 = Module.builder().id(1L).title("Module 1").course(course).build();
        Module module2 = Module.builder().id(2L).title("Module 2").course(course).build();
        Module module3 = Module.builder().id(3L).title("Module 3").course(course).build();

        currentModulesMap.put(1L, module1);
        currentModulesMap.put(2L, module2);
        currentModulesMap.put(3L, module3);
    }

    @DisplayName("MergeModules with various scenarios")
    @ParameterizedTest(name = "{index} => moduleDTOS={0}, countUpdatedModules={1}, " +
            "expectedTitles={2}, countModulesForDeletion={3}")
    @MethodSource("moduleScenariosProvider")
    public void mergeModules_VariousScenarios(List<UpdateModuleInCourseDTO> moduleDTOS,
                                              int countUpdatedModules,
                                              List<String> expectedTitles,
                                              int countModulesForDeletion) {
        //Arrange
        lenient().when(moduleRepository.save(any(Module.class))).thenAnswer(invocation -> invocation.getArgument(0));
        lenient().when(lessonService.updateLessons(anyList(), any(Module.class))).thenReturn(new ArrayList<>());

        // Act
        List<Module> updatedModules = ModuleService.mergeModules(moduleDTOS, modelMapper, currentModulesMap, course,
                moduleRepository, lessonService);

        // Assert
        assertEquals(countUpdatedModules, updatedModules.size());
        for (int i = 0; i < countUpdatedModules; i++) {
            assertEquals(expectedTitles.get(i), updatedModules.get(i).getTitle());
        }
        assertEquals(countModulesForDeletion, currentModulesMap.size());

        verify(moduleRepository, times(countUpdatedModules)).save(any(Module.class));
        verify(lessonService, times(countUpdatedModules)).updateLessons(anyList(), any(Module.class));
    }

    private Stream<Arguments> moduleScenariosProvider() {
        return Stream.of(
                Arguments.of( // 1
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title("Updated Module 1").lessons(Collections.emptyList()).build(),
                                UpdateModuleInCourseDTO.builder().id(2L).title("Updated Module 2").lessons(Collections.emptyList()).build()
                        ),
                        2,
                        List.of("Updated Module 1", "Updated Module 2"),
                        1
                ),
                Arguments.of( // 2
                        List.of(
                                UpdateModuleInCourseDTO.builder().title("New Module 1").lessons(Collections.emptyList()).build(),
                                UpdateModuleInCourseDTO.builder().title("New Module 2").lessons(Collections.emptyList()).build()
                        ),
                        2,
                        List.of("New Module 1", "New Module 2"),
                        3
                ),
                Arguments.of( // 3
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title("Updated Module 1").lessons(Collections.emptyList()).build(),
                                UpdateModuleInCourseDTO.builder().title("New Module 3").lessons(Collections.emptyList()).build(),
                                UpdateModuleInCourseDTO.builder().id(2L).title("Updated Module 2").lessons(Collections.emptyList()).build()
                        ),
                        3,
                        List.of("Updated Module 1", "New Module 3", "Updated Module 2"),
                        1
                ),
                Arguments.of( // 4
                        Collections.emptyList(),
                        0,
                        Collections.emptyList(),
                        3
                ),
                Arguments.of( // 5
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title("Updated Module 1").lessons(Collections.emptyList()).build(),
                                UpdateModuleInCourseDTO.builder().title("New Module 3").lessons(Collections.emptyList()).build()
                        ),
                        2,
                        List.of("Updated Module 1", "New Module 3"),
                        2
                ),
                Arguments.of(
                        List.of(
                                UpdateModuleInCourseDTO.builder().id(1L).title(null).lessons(Collections.emptyList()).build(),
                                UpdateModuleInCourseDTO.builder().id(3L).title(null).lessons(Collections.emptyList()).build()
                        ),
                        2,
                        List.of("Module 1", "Module 3"),
                        1
                )
        );
    }
}