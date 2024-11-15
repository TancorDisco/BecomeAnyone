package ru.sweetbun.becomeanyone.contract;

import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.course.CourseResponse;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;

import java.util.List;

public interface CourseService {

    CourseResponse createCourse(CourseRequest<CreateModuleRequest> rq);
    List<CourseResponse> getAllCourses(Long teacherId, String q);
    CourseResponse getCourseById(Long id);
    CourseResponse updateCourseById(Long id, CourseRequest<UpdateModuleInCourseRequest> rq);
    long deleteCourseById(Long id);
}
