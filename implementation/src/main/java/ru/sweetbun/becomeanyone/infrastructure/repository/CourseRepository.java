package ru.sweetbun.becomeanyone.infrastructure.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.domain.entity.Course;
import ru.sweetbun.becomeanyone.domain.entity.User;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long>, JpaSpecificationExecutor<Course> {

    static Specification<Course> hasTeacher(User teacher) {
        return ((root, query, cb) -> teacher == null
                ? null : cb.equal(root.get("teacher"), teacher));
    }

    static Specification<Course> hasTitle(String title) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }
}
