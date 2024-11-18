package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.test.request.TestRequest;
import ru.sweetbun.becomeanyone.dto.test.request.TestToCheckRequest;
import ru.sweetbun.becomeanyone.dto.test.response.TestResponse;

import java.util.List;
import java.util.Map;

@RequestMapping("/courses/{courseId}/modules/{moduleId}/lessons/{lessonId}/tests")
@FeignClient(name = "testService", url = "http://localhost:8080")
public interface TestServiceClient {

    @PostMapping
    TestResponse createTest(@RequestBody TestRequest testRequest, @PathVariable("lessonId") Long lessonId);

    @GetMapping
    List<TestResponse> getAllTestsByLesson(@PathVariable("lessonId") Long lessonId);

    @GetMapping("{id}")
    TestResponse getTestById(@PathVariable("id") Long id);

    @PatchMapping("{id}")
    TestResponse updateTest(@RequestBody TestRequest testRequest, @PathVariable("id") Long id);

    @DeleteMapping("{id}")
    long deleteTestById(@PathVariable("id") Long id);

    @PostMapping("{id}/check")
    Map<String, Object> checkTest(@RequestBody TestToCheckRequest testDTO, @PathVariable("id") Long id,
                                  @PathVariable("courseId") Long courseId);
}
