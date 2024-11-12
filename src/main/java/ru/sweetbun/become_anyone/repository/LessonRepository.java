package ru.sweetbun.become_anyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.become_anyone.entity.Lesson;
import ru.sweetbun.become_anyone.entity.Module;

import java.util.List;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    List<Lesson> findAllByModule(Module module);

    List<Lesson> findAllByModuleOrderByOrderNumAsc(Module module);

    List<Lesson> findByOrderNumGreaterThan(int orderNum);
}
