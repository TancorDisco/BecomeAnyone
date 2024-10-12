package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.TestDTO;
import ru.sweetbun.BecomeAnyone.entity.Test;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.TestRepository;

import java.util.List;

@Service
public class TestService {

    private final TestRepository testRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public TestService(TestRepository testRepository, ModelMapper modelMapper) {
        this.testRepository = testRepository;
        this.modelMapper = modelMapper;
    }

    public Test createTest(TestDTO testDTO) {
        Test test = modelMapper.map(testDTO, Test.class);
        return testRepository.save(test);
    }

    public Test getTestById(Long id) {
        return testRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Test.class.getSimpleName(), id));
    }

    public List<Test> getAllTests() {
        return testRepository.findAll();
    }

    public Test updateTest(TestDTO testDTO, Long id) {
        Test test = getTestById(id);
        test = modelMapper.map(testDTO, Test.class);
        return testRepository.save(test);
    }

    public void deleteTestById(Long id) {
        testRepository.deleteById(id);
    }
}
