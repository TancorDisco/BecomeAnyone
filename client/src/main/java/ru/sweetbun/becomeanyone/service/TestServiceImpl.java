package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.contract.TestService;
import ru.sweetbun.becomeanyone.dto.test.request.TestRequest;
import ru.sweetbun.becomeanyone.dto.test.request.TestToCheckRequest;
import ru.sweetbun.becomeanyone.dto.test.response.TestResponse;
import ru.sweetbun.becomeanyone.feign.TestServiceClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TestServiceClient testServiceClient;

    @Override
    public TestResponse createTest(TestRequest testRequest, Long lessonId) {
        return testServiceClient.createTest(testRequest, lessonId);
    }

    @Override
    public List<TestResponse> getAllTestsByLesson(Long lessonId) {
        return testServiceClient.getAllTestsByLesson(lessonId);
    }

    @Override
    public TestResponse getTestById(Long id) {
        return testServiceClient.getTestById(id);
    }

    @Override
    public TestResponse updateTest(TestRequest testRequest, Long id) {
        return testServiceClient.updateTest(testRequest, id);
    }

    @Override
    public long deleteTestById(Long id) {
        return testServiceClient.deleteTestById(id);
    }

    @Override
    public Map<String, Object> checkTest(TestToCheckRequest testDTO, Long id, Long courseId) {
        return testServiceClient.checkTest(testDTO, id, courseId);
    }
}
