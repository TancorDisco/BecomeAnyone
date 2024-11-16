package ru.sweetbun.becomeanyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.entity.Course;
import ru.sweetbun.becomeanyone.entity.Module;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findAllByCourse(Course course);

    List<Module> findAllByCourseOrderByOrderNumAsc(Course course);

    List<Module> findByOrderNumGreaterThan(int orderNum);
}
