package ru.sweetbun.becomeanyone.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.course.CourseResponse;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;

import java.util.List;

@FeignClient(name = "courseService", url = "http://localhost:8080")
public interface CourseServiceClient {

    @PostMapping("/courses")
    CourseResponse createCourse(@RequestBody CourseRequest<CreateModuleRequest> rq);

    @GetMapping("/courses")
    Page<CourseResponse> getAllCourses(@RequestParam(required = false) Long teacherId,
                                       @RequestParam(required = false) String q,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "10") int pageSize);

    @GetMapping("/courses/{id}")
    CourseResponse getCourseById(@PathVariable Long id);

    @PatchMapping("/courses/{id}")
    CourseResponse updateCourseById(@PathVariable Long id, @RequestBody CourseRequest<UpdateModuleInCourseRequest> rq);

    @DeleteMapping("/courses/{id}")
    long deleteCourseById(@PathVariable Long id);
}
