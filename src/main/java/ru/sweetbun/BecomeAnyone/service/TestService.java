package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.TestDTO;
import ru.sweetbun.BecomeAnyone.DTO.toCheck.TestToCheckDTO;
import ru.sweetbun.BecomeAnyone.entity.Lesson;
import ru.sweetbun.BecomeAnyone.entity.Question;
import ru.sweetbun.BecomeAnyone.entity.Test;
import ru.sweetbun.BecomeAnyone.entity.TestResult;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.TestRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class TestService {

    private final LessonService lessonService;

    private final TestRepository testRepository;

    private final ModelMapper modelMapper;

    private final QuestionService questionService;

    private final TestResultService testResultService;

    @Autowired
    public TestService(LessonService lessonService, TestRepository testRepository, ModelMapper modelMapper,
                       @Lazy QuestionService questionService, TestResultService testResultService) {
        this.lessonService = lessonService;
        this.testRepository = testRepository;
        this.modelMapper = modelMapper;
        this.questionService = questionService;
        this.testResultService = testResultService;
    }

    public Test createTest(TestDTO testDTO, Long lessonId) {
        Lesson lesson = lessonService.getLessonById(lessonId);
        Test test = modelMapper.map(testDTO, Test.class);
        test.setLesson(lesson);
        lesson.getTests().add(test);
        return testRepository.save(test);
    }

    public Test getTestById(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Test.class, id));
    }

    public List<Test> getAllTestsByLesson(Long lessonId) {
        return testRepository.findAllTestsByLesson(lessonService.getLessonById(lessonId));
    }

    public Test updateTest(TestDTO testDTO, Long id) {
        Test test = getTestById(id);
        modelMapper.map(testDTO, test);
        return testRepository.save(test);
    }

    public String deleteTestById(Long id) {
        getTestById(id);
        testRepository.deleteById(id);
        return "Test has been deleted with id: " + id;
    }

    public Map<String, Object> checkTest(TestToCheckDTO testDTO, Long id, Long courseId) {
        Test test = getTestById(id);
        List<Question> questions = test.getQuestions();
        Test testToSend = modelMapper.map(test, Test.class);
        List<Question> wrongQuestions = questionService.checkQuestions(testDTO.questions(), questions);
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
