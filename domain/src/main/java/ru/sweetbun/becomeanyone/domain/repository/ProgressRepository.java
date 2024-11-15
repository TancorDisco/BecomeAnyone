package ru.sweetbun.becomeanyone.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.domain.entity.Progress;

@Repository
public interface ProgressRepository extends JpaRepository<Progress, Long> {
}
