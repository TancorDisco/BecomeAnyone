package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.contract.TestService;
import ru.sweetbun.becomeanyone.dto.test.request.TestRequest;
import ru.sweetbun.becomeanyone.dto.test.request.TestToCheckRequest;
import ru.sweetbun.becomeanyone.domain.entity.Lesson;
import ru.sweetbun.becomeanyone.domain.entity.Question;
import ru.sweetbun.becomeanyone.domain.entity.Test;
import ru.sweetbun.becomeanyone.domain.entity.TestResult;
import ru.sweetbun.becomeanyone.dto.test.response.TestResponse;
import ru.sweetbun.becomeanyone.dto.testresult.TestResultResponse;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.domain.repository.TestRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TestServiceImpl implements TestService {

    private final LessonServiceImpl lessonServiceImpl;

    private final TestRepository testRepository;

    private final ModelMapper modelMapper;
    @Lazy
    private final QuestionServiceImpl questionServiceImpl;

    private final TestResultService testResultService;

    @Override
    @Transactional
    public TestResponse createTest(TestRequest testRequest, Long lessonId) {
        Lesson lesson = lessonServiceImpl.fetchLessonById(lessonId);
        Test test = modelMapper.map(testRequest, Test.class);
        test.setLesson(lesson);
        lesson.getTests().add(test);
        Test savedTest = testRepository.save(test);
        return modelMapper.map(savedTest, TestResponse.class);
    }

    @Override
    public TestResponse getTestById(Long id) {
        Test test = fetchTestById(id);
        return modelMapper.map(test, TestResponse.class);
    }

    public Test fetchTestById(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Test.class, id));
    }

    @Override
    public List<TestResponse> getAllTestsByLesson(Long lessonId) {
        return testRepository.findAllTestsByLesson(lessonServiceImpl.fetchLessonById(lessonId)).stream()
                .map(test -> modelMapper.map(test, TestResponse.class))
                .toList();
    }

    @Override
    @Transactional
    public TestResponse updateTest(TestRequest testRequest, Long id) {
        Test test = fetchTestById(id);
        modelMapper.map(testRequest, test);
        Test savedTest = testRepository.save(test);
        return modelMapper.map(savedTest, TestResponse.class);
    }

    @Override
    @Transactional
    public long deleteTestById(Long id) {
        fetchTestById(id);
        testRepository.deleteById(id);
        return id;
    }

    @Transactional
    @Override
    public Map<String, Object> checkTest(TestToCheckRequest testDTO, Long id, Long courseId) {
        Test test = fetchTestById(id);
        List<Question> questions = test.getQuestions();
        Test testToSend = modelMapper.map(test, Test.class);
        List<Question> wrongQuestions = questionServiceImpl.checkQuestions(testDTO.questions(), questions);
        testToSend.setQuestions(wrongQuestions);
        TestResult testResult = testResultService.createTestResult(test,
                calculatePercent(wrongQuestions.size(), questions.size()), courseId);

        TestResponse testResponse = modelMapper.map(testToSend, TestResponse.class);
        TestResultResponse testResultResponse = modelMapper.map(testResult, TestResultResponse.class);
        Map<String, Object> response = new HashMap<>();
        response.put("test", testResponse);
        response.put("testResult", testResultResponse);
        return response;
    }

    private double calculatePercent(int wrong, int all) {
        return 100.0 - Math.round((double) wrong / all * 1000) / 10.0;
    }
}
