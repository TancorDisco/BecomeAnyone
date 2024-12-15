package ru.sweetbun.becomeanyone.contract;

import org.springframework.data.domain.Page;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.course.CourseResponse;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;

public interface CourseService {

    CourseResponse createCourse(CourseRequest<CreateModuleRequest> rq);
    Page<CourseResponse> getAllCourses(Long teacherId, String q, int page, int pageSize);
    CourseResponse getCourseById(Long id);
    CourseResponse updateCourseById(Long id, CourseRequest<UpdateModuleInCourseRequest> rq);
    long deleteCourseById(Long id);
}
