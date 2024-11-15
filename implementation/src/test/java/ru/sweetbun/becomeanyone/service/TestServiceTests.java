package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.becomeanyone.dto.TestDTO;
import ru.sweetbun.becomeanyone.dto.tocheck.QuestionToCheckDTO;
import ru.sweetbun.becomeanyone.dto.tocheck.TestToCheckDTO;
import ru.sweetbun.becomeanyone.domain.service.LessonService;
import ru.sweetbun.becomeanyone.domain.service.QuestionService;
import ru.sweetbun.becomeanyone.domain.service.TestResultService;
import ru.sweetbun.becomeanyone.domain.service.TestService;
import ru.sweetbun.becomeanyone.infrastructure.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.domain.entity.Lesson;
import ru.sweetbun.becomeanyone.domain.entity.Question;
import ru.sweetbun.becomeanyone.domain.entity.TestResult;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.infrastructure.repository.TestRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestServiceTests {

    @Mock
    private LessonService lessonService;

    @Mock
    private TestRepository testRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private QuestionService questionService;

    @Mock
    private TestResultService testResultService;

    @InjectMocks
    private TestService testService;

    private TestDTO testDTO;
    private ru.sweetbun.becomeanyone.domain.entity.Test test;
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        testService = new TestService(lessonService, testRepository, modelMapper, questionService, testResultService);

        testDTO = new TestDTO("Test Name", "");
        lesson = Lesson.builder().id(1L).build();
        test = ru.sweetbun.becomeanyone.domain.entity.Test.builder().id(1L).lesson(lesson).build();
    }

    @Test
    void createTest_LessonExists_TestCreated() {
        when(lessonService.getLessonById(1L)).thenReturn(lesson);
        when(testRepository.save(any(ru.sweetbun.becomeanyone.domain.entity.Test.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        ru.sweetbun.becomeanyone.domain.entity.Test result = testService.createTest(testDTO, 1L);

        assertNotNull(result);
        assertEquals("Test Name", result.getTitle());
        verify(testRepository).save(any(ru.sweetbun.becomeanyone.domain.entity.Test.class));
    }

    @Test
    void createTest_LessonNotFound_ThrowsResourceNotFoundException() {
        when(lessonService.getLessonById(1L)).thenThrow(new ResourceNotFoundException(Lesson.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> testService.createTest(testDTO, 1L));
    }

    @Test
    void getTestById_TestExists_ReturnsTest() {
        when(testRepository.findById(1L)).thenReturn(Optional.of(test));

        ru.sweetbun.becomeanyone.domain.entity.Test result = testService.getTestById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTestById_TestNotFound_ThrowsResourceNotFoundException() {
        when(testRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> testService.getTestById(1L));
    }

    @Test
    void getAllTestsByLesson_LessonExists_ReturnsTestList() {
        when(lessonService.getLessonById(1L)).thenReturn(lesson);
        when(testRepository.findAllTestsByLesson(lesson)).thenReturn(List.of(test, test));

        // Act
        List<ru.sweetbun.becomeanyone.domain.entity.Test> result = testService.getAllTestsByLesson(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void updateTest_TestExists_TestUpdated() {
        when(testRepository.findById(1L)).thenReturn(Optional.of(test));
        when(testRepository.save(any(ru.sweetbun.becomeanyone.domain.entity.Test.class))).thenReturn(test);

        ru.sweetbun.becomeanyone.domain.entity.Test result = testService.updateTest(testDTO, 1L);

        assertNotNull(result);
        assertEquals("Test Name", result.getTitle());
    }

    @Test
    void updateTest_TestNotFound_ThrowsResourceNotFoundException() {
        when(testRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> testService.updateTest(testDTO, 1L));
    }

    @Test
    void deleteTestById_TestExists_TestDeleted() {
        when(testRepository.findById(1L)).thenReturn(Optional.of(test));

        long deletedId = testService.deleteTestById(1L);

        assertEquals(1L, deletedId);
        verify(testRepository).deleteById(1L);
    }

    @Test
    void deleteTestById_TestNotFound_ThrowsResourceNotFoundException() {
        when(testRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> testService.deleteTestById(1L));
    }

    @Test
    void checkTest_ValidData_ReturnsTestWithResults() {
        TestToCheckDTO testToCheckDTO = new TestToCheckDTO(List.of(
                new QuestionToCheckDTO(1L, true, null)));
        test.setQuestions(List.of(Question.builder().id(1L).build()));
        when(testRepository.findById(1L)).thenReturn(Optional.of(test));
        lenient().when(questionService.checkQuestions(anyList(), anyList())).thenReturn(List.of(new Question()));
        when(testResultService.createTestResult(any(), anyDouble(), anyLong())).thenReturn(new TestResult());

        var result = testService.checkTest(testToCheckDTO, 1L, 1L);

        assertNotNull(result);
        assertNotNull(result.get("test"));
        assertNotNull(result.get("testResult"));
    }

    @Test
    void checkTest_TestNotFound_ThrowsResourceNotFoundException() {
        TestToCheckDTO testToCheckDTO = new TestToCheckDTO(null);
        when(testRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> testService.checkTest(testToCheckDTO, 1L, 1L));
    }
}