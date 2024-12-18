package ru.sweetbun.becomeanyone.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.User;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    static Specification<Course> hasTeacher(User teacher) {
        return ((root, query, cb) -> teacher == null
                ? null : cb.equal(root.get("teacher"), teacher));
    }

    static Specification<Course> hasTitle(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    @Query("SELECT c.id FROM Course c WHERE c.teacher.id = :teacherId")
    List<Long> findCourseIdsByTeacherId(@Param("teacherId") Long teacherId);
}
