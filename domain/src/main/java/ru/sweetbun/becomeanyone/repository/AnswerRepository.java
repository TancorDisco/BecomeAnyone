package ru.sweetbun.becomeanyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.entity.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
