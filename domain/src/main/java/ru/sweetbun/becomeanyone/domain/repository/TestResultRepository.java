package ru.sweetbun.becomeanyone.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.domain.entity.Progress;
import ru.sweetbun.becomeanyone.domain.entity.Test;
import ru.sweetbun.becomeanyone.domain.entity.TestResult;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    boolean existsByTestAndProgressAndPercentGreaterThanEqual(Test test, Progress progress, double acceptablePercentage);
}
