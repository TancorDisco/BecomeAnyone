package ru.sweetbun.BecomeAnyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.BecomeAnyone.entity.Lesson;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {
}
