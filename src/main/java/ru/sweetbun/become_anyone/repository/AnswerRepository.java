package ru.sweetbun.become_anyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.become_anyone.entity.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
