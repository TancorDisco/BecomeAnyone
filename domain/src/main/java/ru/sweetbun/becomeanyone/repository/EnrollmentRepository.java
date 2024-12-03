package ru.sweetbun.becomeanyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.Enrollment;
import ru.sweetbun.becomeanyone.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findAllByStudent(User student);

    void deleteByStudentAndCourse(User student, Course courses);

    Optional<Enrollment> findByStudentAndCourse(User student, Course course);

    @Query("SELECT e.student FROM Enrollment e WHERE e.course.id IN :courseIds")
    List<User> findStudentsByCourseIds(@Param("courseIds") List<Long> courseIds);
}
