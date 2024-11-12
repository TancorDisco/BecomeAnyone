package ru.sweetbun.become_anyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.become_anyone.entity.Course;
import ru.sweetbun.become_anyone.entity.Module;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findAllByCourse(Course course);

    List<Module> findAllByCourseOrderByOrderNumAsc(Course course);

    List<Module> findByOrderNumGreaterThan(int orderNum);
}
