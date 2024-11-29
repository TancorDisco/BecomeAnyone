package ru.sweetbun.becomeanyone.contract;

import ru.sweetbun.becomeanyone.dto.test.request.TestRequest;
import ru.sweetbun.becomeanyone.dto.test.request.TestToCheckRequest;
import ru.sweetbun.becomeanyone.dto.test.response.TestResponse;

import java.util.List;
import java.util.Map;

public interface TestService {

    TestResponse createTest(TestRequest testRequest, Long lessonId);
    List<TestResponse> getAllTestsByLesson(Long lessonId);
    TestResponse getTestById(Long id);
    TestResponse updateTest(TestRequest testRequest, Long id);
    long deleteTestById(Long id);
    Map<String, Object> checkTest(TestToCheckRequest testDTO, Long id, Long courseId);
}
