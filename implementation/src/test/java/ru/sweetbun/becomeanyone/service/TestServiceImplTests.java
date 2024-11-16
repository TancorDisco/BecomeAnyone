package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.becomeanyone.dto.test.request.TestRequest;
import ru.sweetbun.becomeanyone.dto.question.request.QuestionToCheckRequest;
import ru.sweetbun.becomeanyone.dto.test.request.TestToCheckRequest;
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.domain.entity.Lesson;
import ru.sweetbun.becomeanyone.domain.entity.Question;
import ru.sweetbun.becomeanyone.domain.entity.TestResult;
import ru.sweetbun.becomeanyone.dto.test.response.TestResponse;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.domain.repository.TestRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TestServiceImplTests {

    @Mock
    private LessonServiceImpl lessonServiceImpl;

    @Mock
    private TestRepository testRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private QuestionServiceImpl questionServiceImpl;

    @Mock
    private TestResultService testResultService;

    @InjectMocks
    private TestServiceImpl testServiceImpl;

    private TestRequest testRequest;
    private ru.sweetbun.becomeanyone.domain.entity.Test test;
    private Lesson lesson;

    @BeforeEach
    void setUp() {
        testServiceImpl = new TestServiceImpl(lessonServiceImpl, testRepository, modelMapper, questionServiceImpl, testResultService);

        testRequest = new TestRequest("Test Name", "");
        lesson = Lesson.builder().id(1L).build();
        test = ru.sweetbun.becomeanyone.domain.entity.Test.builder().id(1L).lesson(lesson).build();
    }

    @Test
    void createTest_LessonExists_TestCreated() {
        when(lessonServiceImpl.fetchLessonById(1L)).thenReturn(lesson);
        when(testRepository.save(any(ru.sweetbun.becomeanyone.domain.entity.Test.class))).thenAnswer(
                invocation -> invocation.getArgument(0));

        TestResponse result = testServiceImpl.createTest(testRequest, 1L);

        assertNotNull(result);
        assertEquals("Test Name", result.getTitle());
        verify(testRepository).save(any(ru.sweetbun.becomeanyone.domain.entity.Test.class));
    }

    @Test
    void createTest_LessonNotFound_ThrowsResourceNotFoundException() {
        when(lessonServiceImpl.fetchLessonById(1L)).thenThrow(new ResourceNotFoundException(Lesson.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> testServiceImpl.createTest(testRequest, 1L));
    }

    @Test
    void fetchTestById_TestExists_ReturnsTest() {
        when(testRepository.findById(1L)).thenReturn(Optional.of(test));

        ru.sweetbun.becomeanyone.domain.entity.Test result = testServiceImpl.fetchTestById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void fetchTestById_TestNotFound_ThrowsResourceNotFoundException() {
        when(testRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> testServiceImpl.fetchTestById(1L));
    }

    @Test
    void getAllTestsByLesson_LessonExists_ReturnsTestList() {
        when(lessonServiceImpl.fetchLessonById(1L)).thenReturn(lesson);
        when(testRepository.findAllTestsByLesson(lesson)).thenReturn(List.of(test, test));

        // Act
        List<TestResponse> result = testServiceImpl.getAllTestsByLesson(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void updateTest_TestExists_TestUpdated() {
        when(testRepository.findById(1L)).thenReturn(Optional.of(test));
        when(testRepository.save(any(ru.sweetbun.becomeanyone.domain.entity.Test.class))).thenReturn(test);

        TestResponse result = testServiceImpl.updateTest(testRequest, 1L);

        assertNotNull(result);
        assertEquals("Test Name", result.getTitle());
    }

    @Test
    void updateTest_TestNotFound_ThrowsResourceNotFoundException() {
        when(testRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> testServiceImpl.updateTest(testRequest, 1L));
    }

    @Test
    void deleteTestById_TestExists_TestDeleted() {
        when(testRepository.findById(1L)).thenReturn(Optional.of(test));

        long deletedId = testServiceImpl.deleteTestById(1L);

        assertEquals(1L, deletedId);
        verify(testRepository).deleteById(1L);
    }

    @Test
    void deleteTestById_TestNotFound_ThrowsResourceNotFoundException() {
        when(testRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> testServiceImpl.deleteTestById(1L));
    }

    @Test
    void checkTest_ValidData_ReturnsTestWithResults() {
        TestToCheckRequest testToCheckRequest = new TestToCheckRequest(List.of(
                new QuestionToCheckRequest(1L, true, null)));
        test.setQuestions(List.of(Question.builder().id(1L).build()));
        when(testRepository.findById(1L)).thenReturn(Optional.of(test));
        lenient().when(questionServiceImpl.checkQuestions(anyList(), anyList())).thenReturn(List.of(new Question()));
        when(testResultService.createTestResult(any(), anyDouble(), anyLong())).thenReturn(new TestResult());

        var result = testServiceImpl.checkTest(testToCheckRequest, 1L, 1L);

        assertNotNull(result);
        assertNotNull(result.get("test"));
        assertNotNull(result.get("testResult"));
    }

    @Test
    void checkTest_TestNotFound_ThrowsResourceNotFoundException() {
        TestToCheckRequest testToCheckRequest = new TestToCheckRequest(null);
        when(testRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> testServiceImpl.checkTest(testToCheckRequest, 1L, 1L));
    }
}