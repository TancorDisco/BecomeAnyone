package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.TestDTO;
import ru.sweetbun.BecomeAnyone.entity.Lesson;
import ru.sweetbun.BecomeAnyone.entity.Test;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.TestRepository;

import java.util.List;

@Service
public class TestService {

    private final LessonService lessonService;

    private final TestRepository testRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public TestService(LessonService lessonService, TestRepository testRepository, ModelMapper modelMapper) {
        this.lessonService = lessonService;
        this.testRepository = testRepository;
        this.modelMapper = modelMapper;
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
                .orElseThrow(() -> new ResourceNotFoundException(Test.class.getSimpleName(), id));
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
}
