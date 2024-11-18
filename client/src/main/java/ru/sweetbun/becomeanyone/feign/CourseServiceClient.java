package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.course.CourseResponse;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;

import java.util.List;

@RequestMapping("/courses")
@FeignClient(name = "courseService", url = "http://localhost:8080")
public interface CourseServiceClient {

    @PostMapping
    CourseResponse createCourse(@RequestBody CourseRequest<CreateModuleRequest> rq);

    @GetMapping
    List<CourseResponse> getAllCourses(@RequestParam(required = false) Long teacherId,
                                       @RequestParam(required = false) String q);

    @GetMapping("{id}")
    CourseResponse getCourseById(@PathVariable Long id);

    @PatchMapping("{id}")
    CourseResponse updateCourseById(@PathVariable Long id, @RequestBody CourseRequest<UpdateModuleInCourseRequest> rq);

    @DeleteMapping("{id}")
    long deleteCourseById(@PathVariable Long id);
}
