package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.contract.CourseService;
import ru.sweetbun.becomeanyone.dto.course.CourseRequest;
import ru.sweetbun.becomeanyone.dto.course.CourseResponse;
import ru.sweetbun.becomeanyone.dto.module.request.CreateModuleRequest;
import ru.sweetbun.becomeanyone.dto.module.request.UpdateModuleInCourseRequest;
import ru.sweetbun.becomeanyone.feign.CourseServiceClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseServiceClient courseServiceClient;

    @Override
    public CourseResponse createCourse(CourseRequest<CreateModuleRequest> rq) {
        return courseServiceClient.createCourse(rq);
    }

    @Override
    public List<CourseResponse> getAllCourses(Long teacherId, String q) {
        return courseServiceClient.getAllCourses(teacherId, q);
    }

    @Override
    public CourseResponse getCourseById(Long id) {
        return courseServiceClient.getCourseById(id);
    }

    @Override
    public CourseResponse updateCourseById(Long id, CourseRequest<UpdateModuleInCourseRequest> rq) {
        return courseServiceClient.updateCourseById(id, rq);
    }

    @Override
    public long deleteCourseById(Long id) {
        return courseServiceClient.deleteCourseById(id);
    }
}
