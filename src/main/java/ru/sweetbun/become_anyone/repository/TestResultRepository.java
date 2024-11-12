package ru.sweetbun.become_anyone.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.sweetbun.become_anyone.entity.Progress;
import ru.sweetbun.become_anyone.entity.Test;
import ru.sweetbun.become_anyone.entity.TestResult;

@Repository
public interface TestResultRepository extends JpaRepository<TestResult, Long> {

    boolean existsByTestAndProgressAndPercentGreaterThanEqual(Test test, Progress progress, double acceptablePercentage);
}
