package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
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
import ru.sweetbun.becomeanyone.dto.ContentDTO;
import ru.sweetbun.becomeanyone.dto.lesson.request.CreateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonInCourseRequest;
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.domain.entity.Content;
import ru.sweetbun.becomeanyone.domain.entity.Lesson;
import ru.sweetbun.becomeanyone.domain.entity.Module;
import ru.sweetbun.becomeanyone .exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.domain.repository.LessonRepository;

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

    @Mock
    private ContentService contentService;

    @InjectMocks
    private LessonService lessonService;

    private Module module;
    private Map<Long, Lesson> currentLessonsMap;

    @BeforeEach
    public void setUp() {
        lessonService = new LessonService(lessonRepository, modelMapper, moduleService, contentService);

        module = Module.builder().id(1L).title("Module 1").build();

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
        CreateLessonRequest lessonDTO = CreateLessonRequest.builder().build();
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
        CreateLessonRequest lessonDTO = CreateLessonRequest.builder().build();

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
        UpdateLessonRequest updateLessonRequest = UpdateLessonRequest.builder().content(new ContentDTO("", "")).build();
        Lesson lesson = currentLessonsMap.get(1L);
        Content content =  Content.builder().id(1L).build();
        lesson.setContent(content);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(lesson)).thenReturn(lesson);
        when(contentService.updateContent(any(ContentDTO.class), eq(content))).thenReturn(content);

        Lesson result = lessonService.updateLesson(updateLessonRequest, 1L);

        assertEquals(lesson, result);
        verify(lessonRepository).save(lesson);
    }

    @Test
    void updateLesson_LessonDoesNotExist_ShouldThrowException() {
        UpdateLessonRequest updateLessonRequest = UpdateLessonRequest.builder().build();

        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lessonService.updateLesson(updateLessonRequest, 1L));
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
        List<UpdateLessonInCourseRequest> lessonDTOS = List.of(
                new UpdateLessonInCourseRequest(1L, "Updated Lesson 1", 1),
                new UpdateLessonInCourseRequest(null, "New Lesson", 2)
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
        List<CreateLessonRequest> lessonDTOS = List.of(
                CreateLessonRequest.builder().title("Lesson A").build(),
                CreateLessonRequest.builder().title("Lesson B").build()
        );

        lessonService.createLessons(lessonDTOS, module);

        verify(lessonRepository).saveAll(anyList());
    }

    @Test
    void createLessons_EmptyList_ShouldNotSaveAnything() {
        List<CreateLessonRequest> emptyLessonDTOS = new ArrayList<>();

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

    @ParameterizedTest
    @MethodSource("provideTestData")
    void testUpdateLessons(List<UpdateLessonInCourseRequest> lessonDTOS, List<Lesson> expectedLessons) {
        // Arrange
        module.setLessons(new ArrayList<>(currentLessonsMap.values()));

        // Act
        List<Lesson> updatedLessons = lessonService.updateLessons(lessonDTOS, module);

        // Assert
        assertEquals(expectedLessons.size(), updatedLessons.size());
        for (int i = 0; i < expectedLessons.size(); i++) {
            assertEquals(expectedLessons.get(i).getTitle(), updatedLessons.get(i).getTitle());
            assertEquals(expectedLessons.get(i).getOrderNum(), updatedLessons.get(i).getOrderNum());
        }
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                // 1: Add new lessons and delete old
                Arguments.of(
                        List.of(
                                UpdateLessonInCourseRequest.builder().title("New Lesson 1").orderNum(4).build(),
                                UpdateLessonInCourseRequest.builder().title("New Lesson 2").orderNum(5).build()
                        ),
                        List.of(
                                Lesson.builder().id(4L).title("New Lesson 1").orderNum(4).build(),
                                Lesson.builder().id(5L).title("New Lesson 2").orderNum(5).build()
                        )
                ),
                // 2: Update existing lessons and delete old
                Arguments.of(
                        List.of(
                                UpdateLessonInCourseRequest.builder().id(1L).title("Updated Lesson 1").orderNum(1).build(),
                                UpdateLessonInCourseRequest.builder().id(2L).title("Updated Lesson 2").orderNum(2).build()
                        ),
                        List.of(
                                Lesson.builder().id(1L).title("Updated Lesson 1").orderNum(1).build(),
                                Lesson.builder().id(2L).title("Updated Lesson 2").orderNum(2).build()
                        )
                ),
                // 3: Add new and update existing lessons and delete old
                Arguments.of(
                        List.of(
                                UpdateLessonInCourseRequest.builder().id(1L).title("Updated Lesson 1").orderNum(1).build(),
                                UpdateLessonInCourseRequest.builder().title("New Lesson 3").orderNum(4).build()
                        ),
                        List.of(
                                Lesson.builder().id(1L).title("Updated Lesson 1").orderNum(1).build(),
                                Lesson.builder().id(4L).title("New Lesson 3").orderNum(4).build()
                        )
                ),
                // 4: Add new lessons
                Arguments.of(
                        List.of(
                                UpdateLessonInCourseRequest.builder().id(1L).title("Lesson 1").orderNum(1).build(),
                                UpdateLessonInCourseRequest.builder().id(2L).title("Lesson 2").orderNum(2).build(),
                                UpdateLessonInCourseRequest.builder().id(3L).title("Lesson 3").orderNum(3).build(),
                                UpdateLessonInCourseRequest.builder().title("New Lesson 1").orderNum(4).build(),
                                UpdateLessonInCourseRequest.builder().title("New Lesson 2").orderNum(5).build()
                        ),
                        List.of(
                                Lesson.builder().id(1L).title("Lesson 1").orderNum(1).build(),
                                Lesson.builder().id(2L).title("Lesson 2").orderNum(2).build(),
                                Lesson.builder().id(3L).title("Lesson 3").orderNum(3).build(),
                                Lesson.builder().id(4L).title("New Lesson 1").orderNum(4).build(),
                                Lesson.builder().id(5L).title("New Lesson 2").orderNum(5).build()
                        )
                ),
                // 5: Update existing lessons
                Arguments.of(
                        List.of(
                                UpdateLessonInCourseRequest.builder().id(1L).title("Updated Lesson 1").orderNum(1).build(),
                                UpdateLessonInCourseRequest.builder().id(2L).title("Updated Lesson 2").orderNum(2).build(),
                                UpdateLessonInCourseRequest.builder().id(3L).title("Lesson 3").orderNum(3).build()
                        ),
                        List.of(
                                Lesson.builder().id(1L).title("Updated Lesson 1").orderNum(1).build(),
                                Lesson.builder().id(2L).title("Updated Lesson 2").orderNum(2).build(),
                                Lesson.builder().id(3L).title("Lesson 3").orderNum(3).build()
                        )
                ),
                // 6: Add new and update existing lessons
                Arguments.of(
                        List.of(
                                UpdateLessonInCourseRequest.builder().id(1L).title("Updated Lesson 1").orderNum(1).build(),
                                UpdateLessonInCourseRequest.builder().id(2L).title("Lesson 2").orderNum(2).build(),
                                UpdateLessonInCourseRequest.builder().id(3L).title("Lesson 3").orderNum(3).build(),
                                UpdateLessonInCourseRequest.builder().title("New Lesson 3").orderNum(4).build()
                        ),
                        List.of(
                                Lesson.builder().id(1L).title("Updated Lesson 1").orderNum(1).build(),
                                Lesson.builder().id(2L).title("Lesson 2").orderNum(2).build(),
                                Lesson.builder().id(3L).title("Lesson 3").orderNum(3).build(),
                                Lesson.builder().id(4L).title("New Lesson 3").orderNum(4).build()
                        )
                )
        );
    }
}