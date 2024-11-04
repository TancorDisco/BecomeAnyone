package ru.sweetbun.BecomeAnyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.Enrollment;
import ru.sweetbun.BecomeAnyone.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findAllByStudent(User student);

    void deleteByStudentAndCourse(User student, Course courses);

    Optional<Enrollment> findByStudentAndCourse(User student, Course course);
}
