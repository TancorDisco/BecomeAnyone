package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.dto.test.request.TestRequest;
import ru.sweetbun.becomeanyone.dto.test.request.TestToCheckRequest;
import ru.sweetbun.becomeanyone.domain.entity.Lesson;
import ru.sweetbun.becomeanyone.domain.entity.Question;
import ru.sweetbun.becomeanyone.domain.entity.Test;
import ru.sweetbun.becomeanyone.domain.entity.TestResult;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.domain.repository.TestRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class TestService {

    private final LessonServiceImpl lessonServiceImpl;

    private final TestRepository testRepository;

    private final ModelMapper modelMapper;
    @Lazy
    private final QuestionServiceImpl questionServiceImpl;

    private final TestResultService testResultService;

    @Transactional
    public Test createTest(TestRequest testRequest, Long lessonId) {
        Lesson lesson = lessonServiceImpl.fetchLessonById(lessonId);
        Test test = modelMapper.map(testRequest, Test.class);
        test.setLesson(lesson);
        lesson.getTests().add(test);
        return testRepository.save(test);
    }

    public Test getTestById(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Test.class, id));
    }

    public List<Test> getAllTestsByLesson(Long lessonId) {
        return testRepository.findAllTestsByLesson(lessonServiceImpl.fetchLessonById(lessonId));
    }

    @Transactional
    public Test updateTest(TestRequest testRequest, Long id) {
        Test test = getTestById(id);
        modelMapper.map(testRequest, test);
        return testRepository.save(test);
    }

    @Transactional
    public long deleteTestById(Long id) {
        getTestById(id);
        testRepository.deleteById(id);
        return id;
    }

    public Map<String, Object> checkTest(TestToCheckRequest testDTO, Long id, Long courseId) {
        Test test = getTestById(id);
        List<Question> questions = test.getQuestions();
        Test testToSend = modelMapper.map(test, Test.class);
        List<Question> wrongQuestions = questionServiceImpl.checkQuestions(testDTO.questions(), questions);
        testToSend.setQuestions(wrongQuestions);
        TestResult testResult = testResultService.createTestResult(test,
                calculatePercent(wrongQuestions.size(), questions.size()), courseId);

        Map<String, Object> response = new HashMap<>();
        response.put("test", testToSend);
        response.put("testResult", testResult);
        return response;
    }

    private double calculatePercent(int wrong, int all) {
        return 100.0 - Math.round((double) wrong / all * 1000) / 10.0;
    }
}
