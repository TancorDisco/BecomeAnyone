package ru.sweetbun.BecomeAnyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.modelmapper.ModelMapper;
import ru.sweetbun.BecomeAnyone.DTO.UpdateLessonInCourseDTO;
import ru.sweetbun.BecomeAnyone.config.ModelMapperConfig;
import ru.sweetbun.BecomeAnyone.entity.Lesson;
import ru.sweetbun.BecomeAnyone.entity.Module;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LessonServiceTests {

    private Module module;
    private Map<Long, Lesson> currentLessonsMap;
    private ModelMapper modelMapper;

    @BeforeEach
    public void setUp() {
        module = new Module();
        currentLessonsMap = new HashMap<>();
        modelMapper = ModelMapperConfig.createConfiguredModelMapper();

        Lesson lesson1 = Lesson.builder().id(1L).title("Lesson 1").orderNum(1).module(module).build();
        Lesson lesson2 = Lesson.builder().id(2L).title("Lesson 2").orderNum(2).module(module).build();
        Lesson lesson3 = Lesson.builder().id(3L).title("Lesson 3").orderNum(3).module(module).build();
        currentLessonsMap.put(1L, lesson1);
        currentLessonsMap.put(2L, lesson2);
        currentLessonsMap.put(3L, lesson3);
    }

    @DisplayName("MergeLessons with various scenarios")
    @ParameterizedTest(name = "{index} => lessonDTOS={0}, countUpdatedLessons={1}" +
            ", expectedTitles={2}, countLessonsForDeletion={3}, ")
    @MethodSource("lessonScenariosProvider")
    public void mergeLessons_VariousScenarios(List<UpdateLessonInCourseDTO> lessonDTOS,
                                              int countUpdatedLessons,
                                              List<String> expectedTitles,
                                              int countLessonsForDeletion) {
        // Act
        List<Lesson> updatedLessons = LessonService.mergeLessons(lessonDTOS, modelMapper, currentLessonsMap, module);

        // Assert
        assertEquals(countUpdatedLessons, updatedLessons.size());
        for (int i = 0; i < countUpdatedLessons; i++) {
            assertEquals(expectedTitles.get(i), updatedLessons.get(i).getTitle());
        }
        assertEquals(countLessonsForDeletion, currentLessonsMap.size());
    }

    private Stream<Arguments> lessonScenariosProvider() {
        return Stream.of(
                Arguments.of( // 1
                        List.of(
                                UpdateLessonInCourseDTO.builder().id(1L).title("Updated Lesson 1").build(),
                                UpdateLessonInCourseDTO.builder().id(2L).title("Updated Lesson 2").build()
                        ),
                        2,
                        List.of("Updated Lesson 1", "Updated Lesson 2"),
                        1
                ),
                Arguments.of( // 2
                        List.of(
                                UpdateLessonInCourseDTO.builder().title("New Lesson 1").build(),
                                UpdateLessonInCourseDTO.builder().title("New Lesson 2").build()
                        ),
                        2,
                        List.of("New Lesson 1", "New Lesson 2"),
                        3
                ),
                Arguments.of( // 3
                        List.of(
                                UpdateLessonInCourseDTO.builder().id(1L).title("Updated Lesson 1").build(),
                                UpdateLessonInCourseDTO.builder().title("New Lesson 3").build(),
                                UpdateLessonInCourseDTO.builder().id(2L).title("Updated Lesson 2").build()
                        ),
                        3,
                        List.of("Updated Lesson 1", "New Lesson 3", "Updated Lesson 2"),
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
                                UpdateLessonInCourseDTO.builder().id(1L).title("Updated Lesson 1").build(),
                                UpdateLessonInCourseDTO.builder().title("New Lesson 3").build()
                        ),
                        2,
                        List.of("Updated Lesson 1", "New Lesson 3"),
                        2
                ),
                Arguments.of( // 6
                        List.of(
                                UpdateLessonInCourseDTO.builder().id(1L).title(null).build(),
                                UpdateLessonInCourseDTO.builder().id(3L).title(null).build()
                        ),
                        2,
                        List.of("Lesson 1", "Lesson 3"),
                        1
                )
        );
    }
}