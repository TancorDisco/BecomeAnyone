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
import ru.sweetbun.becomeanyone.contract.eventpublisher.FileDeletionEventPublisher;
import ru.sweetbun.becomeanyone.dto.content.ContentRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.CreateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonRequest;
import ru.sweetbun.becomeanyone.dto.lesson.request.UpdateLessonInCourseRequest;
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.entity.Content;
import ru.sweetbun.becomeanyone.entity.Lesson;
import ru.sweetbun.becomeanyone.entity.Module;
import ru.sweetbun.becomeanyone.dto.lesson.response.LessonResponse;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.LessonRepository;
import ru.sweetbun.becomeanyone.util.CacheServiceProvider;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LessonServiceImplTests {

    @Mock
    private LessonRepository lessonRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private ModuleServiceImpl moduleServiceImpl;

    @Mock
    private ContentService contentService;

    @Mock
    private CacheServiceProvider cacheServiceProvider;

    @Mock
    private FileDeletionEventPublisher fileDeletionEventPublisher;

    @Mock
    private FileServiceImpl fileService;

    @InjectMocks
    private LessonServiceImpl lessonServiceImpl;

    private Module module;
    private Map<Long, Lesson> currentLessonsMap;
    private LessonResponse lessonResponse;

    @BeforeEach
    public void setUp() {
        lessonServiceImpl = new LessonServiceImpl(lessonRepository, modelMapper, moduleServiceImpl, contentService,
                cacheServiceProvider, fileDeletionEventPublisher, fileService);

        module = Module.builder().id(1L).title("Module 1").build();
        lessonResponse = new LessonResponse();

        Lesson lesson1 = Lesson.builder().id(1L).title("Lesson 1").orderNum(1).module(module).content(new Content()).build();
        Lesson lesson2 = Lesson.builder().id(2L).title("Lesson 2").orderNum(2).module(module).content(new Content()).build();
        Lesson lesson3 = Lesson.builder().id(3L).title("Lesson 3").orderNum(3).module(module).content(new Content()).build();

        currentLessonsMap = new HashMap<>();
        currentLessonsMap.put(1L, lesson1);
        currentLessonsMap.put(2L, lesson2);
        currentLessonsMap.put(3L, lesson3);
    }

    @Test
    void createLesson_ValidInput_ShouldReturnCreatedLesson() {
        CreateLessonRequest lessonDTO = CreateLessonRequest.builder().build();

        when(moduleServiceImpl.fetchModuleById(1L)).thenReturn(module);
        when(lessonRepository.save(any(Lesson.class))).thenReturn(currentLessonsMap.get(1L));

        LessonResponse result = lessonServiceImpl.createLesson(lessonDTO, 1L);

        assertNotNull(result);
        verify(lessonRepository).save(any(Lesson.class));
    }

    @Test
    void createLesson_ModuleNotFound_ShouldThrowException() {
        CreateLessonRequest lessonDTO = CreateLessonRequest.builder().build();

        when(moduleServiceImpl.fetchModuleById(1L)).thenThrow(new ResourceNotFoundException(Module.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> lessonServiceImpl.createLesson(lessonDTO, 1L));
    }

    @Test
    void fetchLessonById_LessonExists_ShouldReturnLesson() {
        Lesson lesson = currentLessonsMap.get(1L);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));

        Lesson result = lessonServiceImpl.fetchLessonById(1L);

        assertEquals(lesson, result);
        verify(lessonRepository).findById(1L);
    }

    @Test
    void fetchLessonById_LessonDoesNotExist_ShouldThrowException() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lessonServiceImpl.fetchLessonById(1L));
    }

    @Test
    void updateLesson_LessonExists_ShouldUpdateAndReturnLesson() {
        UpdateLessonRequest updateLessonRequest = UpdateLessonRequest.builder().content(new ContentRequest("", "")).build();
        Lesson lesson = currentLessonsMap.get(1L);
        Content content =  Content.builder().id(1L).build();
        lesson.setContent(content);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(lessonRepository.save(lesson)).thenReturn(lesson);
        when(contentService.updateContent(any(ContentRequest.class), eq(content))).thenReturn(content);

        LessonResponse result = lessonServiceImpl.updateLesson(updateLessonRequest, 1L);

        assertNotNull(result);
        verify(lessonRepository).save(lesson);
    }

    @Test
    void updateLesson_LessonDoesNotExist_ShouldThrowException() {
        UpdateLessonRequest updateLessonRequest = UpdateLessonRequest.builder().build();

        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lessonServiceImpl.updateLesson(updateLessonRequest, 1L));
    }

    @Test
    void deleteLessonById_LessonExists_ShouldDeleteLessonAndReturnId() {
        Lesson lesson = currentLessonsMap.get(1L);

        when(lessonRepository.findById(1L)).thenReturn(Optional.of(lesson));
        when(lessonRepository.findByOrderNumGreaterThan(lesson.getOrderNum()))
                .thenReturn(List.of(currentLessonsMap.get(2L), currentLessonsMap.get(3L)));

        long result = lessonServiceImpl.deleteLessonById(1L);

        assertEquals(1L, result);
        verify(lessonRepository).deleteById(1L);
        assertEquals(1, currentLessonsMap.get(2L).getOrderNum());
        assertEquals(2, currentLessonsMap.get(3L).getOrderNum());
    }

    @Test
    void deleteLessonById_LessonDoesNotExist_ShouldThrowException() {
        when(lessonRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> lessonServiceImpl.deleteLessonById(1L));
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
        List<Lesson> result = lessonServiceImpl.updateLessons(lessonDTOS, module);

        //Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(lessonRepository, times(1)).deleteAll(any());
    }

    @Test
    void updateLessons_EmptyList_ShouldReturnEmptyList() {
        List<Lesson> result = lessonServiceImpl.updateLessons(new ArrayList<>(), module);

        assertTrue(result.isEmpty());
        verify(lessonRepository, never()).deleteAll(any());
    }

    @Test
    void createLessons_ValidInput_ShouldSaveAllLessons() {
        List<CreateLessonRequest> lessonDTOS = List.of(
                CreateLessonRequest.builder().title("Lesson A").build(),
                CreateLessonRequest.builder().title("Lesson B").build()
        );

        lessonServiceImpl.createLessons(lessonDTOS, module);

        verify(lessonRepository).saveAll(anyList());
    }

    @Test
    void createLessons_EmptyList_ShouldNotSaveAnything() {
        List<CreateLessonRequest> emptyLessonDTOS = new ArrayList<>();

        lessonServiceImpl.createLessons(emptyLessonDTOS, module);

        verify(lessonRepository, never()).saveAll(any());
    }

    @Test
    void getAllLessonsByModule_ModuleExists_ShouldReturnLessons() {
        when(moduleServiceImpl.fetchModuleById(1L)).thenReturn(module);
        when(lessonRepository.findAllByModuleOrderByOrderNumAsc(module)).thenReturn(new ArrayList<>(currentLessonsMap.values()));

        List<LessonResponse> result = lessonServiceImpl.getAllLessonsByModule(1L);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(lessonRepository).findAllByModuleOrderByOrderNumAsc(module);
    }

    @Test
    void getAllLessonsByModule_ModuleDoesNotExist_ShouldThrowException() {
        when(moduleServiceImpl.fetchModuleById(1L)).thenThrow(new ResourceNotFoundException(Module.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> lessonServiceImpl.getAllLessonsByModule(1L));
    }

    @ParameterizedTest
    @MethodSource("provideTestData")
    void testUpdateLessons(List<UpdateLessonInCourseRequest> lessonDTOS, List<Lesson> expectedLessons) {
        // Arrange
        module.setLessons(new ArrayList<>(currentLessonsMap.values()));

        // Act
        List<Lesson> updatedLessons = lessonServiceImpl.updateLessons(lessonDTOS, module);

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