package ru.sweetbun.BecomeAnyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.BecomeAnyone.entity.Progress;
import ru.sweetbun.BecomeAnyone.entity.Test;
import ru.sweetbun.BecomeAnyone.entity.TestResult;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    boolean existsByTestAndProgressAndPercentGreaterThanEqual(Test test, Progress progress, double acceptablePercentage);
}
