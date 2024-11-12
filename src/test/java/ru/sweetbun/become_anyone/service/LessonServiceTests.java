package ru.sweetbun.become_anyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.become_anyone.DTO.CreateLessonDTO;
import ru.sweetbun.become_anyone.DTO.UpdateLessonDTO;
import ru.sweetbun.become_anyone.DTO.UpdateLessonInCourseDTO;
import ru.sweetbun.become_anyone.config.ModelMapperConfig;
import ru.sweetbun.become_anyone.entity.Lesson;
import ru.sweetbun.become_anyone.entity.Module;
import ru.sweetbun.become_anyone.exception.ResourceNotFoundException;
import ru.sweetbun.become_anyone.repository.LessonRepository;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LessonServiceTests {

    @Mock
    private LessonRepository lessonRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private ModuleService moduleService;

    @InjectMocks
    private LessonService lessonService;

    private Module module;
    private Map<Long, Lesson> currentLessonsMap;

    @BeforeEach
    public void setUp() {
        lessonService = new LessonService(lessonRepository, modelMapper, moduleService);

        module = Module.builder().id(1L).build();

        Lesson lesson1 = Lesson.builder().id(1L).title("Lesson 1").orderNum(1).module(module).build();
        Lesson lesson2 = Lesson.builder().id(2L).title("Lesson 2").orderNum(2).module(module).build();
        Lesson lesson3 = Lesson.builder().id(3L).title("Lesson 3").orderNum(3).module(module).build();

        currentLessonsMap = new HashMap<>();
        currentLessonsMap.put(1L, lesson1);
        currentLessonsMap.put(2L, lesson2);
        currentLessonsMap.put(3L, lesson3);
    }

    @Test
    void createLesson_ValidInput_ShouldReturnCreatedLesson() {
        CreateLessonDTO lessonDTO = CreateLessonDTO.builder().build();
        Lesson expectedLesson = new Lesson();

        when(moduleService.getModuleById(1L)).thenReturn(module);
        when(lessonRepository.save(any(Lesson.class))).thenReturn(expectedLesson);

        Lesson result = lessonService.createLesson(lessonDTO, 1L);

        assertNotNull(result);
        assertEquals(expectedLesson, result);
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Test
    void createLesson_ModuleNotFound_ShouldThrowException() {
        CreateLessonDTO lessonDTO = CreateLessonDTO.builder().build();

        when(moduleService.getModuleById(1L)).thenThrow(new ResourceNotFoundException(Module.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> lessonService.createLesson(lessonDTO, 1L));
    }

    @Test
    void getLessonById_LessonExists_ShouldReturnLesson() {
        Lesson lesson = currentLessonsMap.get(1L);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        Lesson result = lessonService.getLessonById(1L);

        assertEquals(lesson, result);
        verify(lessonRepository).findById(1L);
    }

    @Test
    void getLessonById_LessonDoesNotExist_ShouldThrowException() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lessonService.getLessonById(1L));
    }

    @Test
    void updateLesson_LessonExists_ShouldUpdateAndReturnLesson() {
        UpdateLessonDTO updateLessonDTO = UpdateLessonDTO.builder().build();
        Lesson lesson = currentLessonsMap.get(1L);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(lesson)).thenReturn(lesson);

        Lesson result = lessonService.updateLesson(updateLessonDTO, 1L);

        assertEquals(lesson, result);
        verify(lessonRepository).save(lesson);
    }

    @Test
    void updateLesson_LessonDoesNotExist_ShouldThrowException() {
        UpdateLessonDTO updateLessonDTO = UpdateLessonDTO.builder().build();

        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lessonService.updateLesson(updateLessonDTO, 1L));
    }

    @Test
    void deleteLessonById_LessonExists_ShouldDeleteLessonAndReturnId() {
        Lesson lesson = currentLessonsMap.get(1L);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(lessonRepository.findByOrderNumGreaterThan(lesson.getOrderNum()))
                .thenReturn(List.of(currentLessonsMap.get(2L), currentLessonsMap.get(3L)));

        long result = lessonService.deleteLessonById(1L);

        assertEquals(1L, result);
        verify(lessonRepository).deleteById(1L);
        assertEquals(1, currentLessonsMap.get(2L).getOrderNum());
        assertEquals(2, currentLessonsMap.get(3L).getOrderNum());
    }

    @Test
    void deleteLessonById_LessonDoesNotExist_ShouldThrowException() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lessonService.deleteLessonById(1L));
    }

    @Test
    void updateLessons_ValidInput_ShouldUpdateAndReturnLessons() {
        //Arrange
        List<UpdateLessonInCourseDTO> lessonDTOS = List.of(
                new UpdateLessonInCourseDTO(1L, "Updated Lesson 1", 1),
                new UpdateLessonInCourseDTO(null, "New Lesson", 2)
        );
        module.getLessons().add(currentLessonsMap.get(3L));

        //Act
        List<Lesson> result = lessonService.updateLessons(lessonDTOS, module);

        //Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(lessonRepository, times(1)).deleteAll(any());
    }

    @Test
    void updateLessons_EmptyList_ShouldReturnEmptyList() {
        List<Lesson> result = lessonService.updateLessons(new ArrayList<>(), module);

        assertTrue(result.isEmpty());
        verify(lessonRepository, never()).deleteAll(any());
    }

    @Test
    void createLessons_ValidInput_ShouldSaveAllLessons() {
        List<CreateLessonDTO> lessonDTOS = List.of(
                CreateLessonDTO.builder().title("Lesson A").build(),
                CreateLessonDTO.builder().title("Lesson B").build()
        );

        lessonService.createLessons(lessonDTOS, module);

        verify(lessonRepository).saveAll(anyList());
    }

    @Test
    void createLessons_EmptyList_ShouldNotSaveAnything() {
        List<CreateLessonDTO> emptyLessonDTOS = new ArrayList<>();

        lessonService.createLessons(emptyLessonDTOS, module);

        verify(lessonRepository, never()).saveAll(any());
    }

    @Test
    void getAllLessonsByModule_ModuleExists_ShouldReturnLessons() {
        when(moduleService.getModuleById(1L)).thenReturn(module);
        when(lessonRepository.findAllByModuleOrderByOrderNumAsc(module)).thenReturn(new ArrayList<>(currentLessonsMap.values()));

        List<Lesson> result = lessonService.getAllLessonsByModule(1L);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(lessonRepository).findAllByModuleOrderByOrderNumAsc(module);
    }

    @Test
    void getAllLessonsByModule_ModuleDoesNotExist_ShouldThrowException() {
        when(moduleService.getModuleById(1L)).thenThrow(new ResourceNotFoundException(Module.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> lessonService.getAllLessonsByModule(1L));
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