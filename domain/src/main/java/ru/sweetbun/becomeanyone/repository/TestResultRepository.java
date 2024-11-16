package ru.sweetbun.becomeanyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.becomeanyone.entity.Progress;
import ru.sweetbun.becomeanyone.entity.Test;
import ru.sweetbun.becomeanyone.entity.TestResult;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    boolean existsByTestAndProgressAndPercentGreaterThanEqual(Test test, Progress progress, double acceptablePercentage);
}
