package ru.sweetbun.becomeanyone.infrastructure.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.domain.entity.Answer;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
}
