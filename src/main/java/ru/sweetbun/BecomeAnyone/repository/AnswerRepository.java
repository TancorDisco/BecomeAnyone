package ru.sweetbun.BecomeAnyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.BecomeAnyone.entity.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
