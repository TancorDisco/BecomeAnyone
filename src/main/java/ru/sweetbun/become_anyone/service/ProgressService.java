package ru.sweetbun.become_anyone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.become_anyone.entity.Course;
import ru.sweetbun.become_anyone.entity.Progress;
import ru.sweetbun.become_anyone.entity.TestResult;
import ru.sweetbun.become_anyone.exception.ResourceNotFoundException;
import ru.sweetbun.become_anyone.repository.ProgressRepository;

import java.util.List;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;

    private final double acceptablePercentage;

    @Autowired
    public ProgressService(ProgressRepository progressRepository,
                           @Value("${test-result.percentage.acceptable}") double acceptablePercentage) {
        this.progressRepository = progressRepository;
        this.acceptablePercentage = acceptablePercentage;
    }

    @Transactional
    public Progress createProgress() {
        return progressRepository.save(new Progress());
    }

    public Progress getProgressById(Long id) {
        return progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Progress.class, id));
    }

    public List<Progress> getAllProgress() {
        return progressRepository.findAll();
    }

    @Transactional
    public double updateProgress(TestResult testResult, Course course) {
        Progress progress = testResult.getProgress();

        if (acceptablePercentage <= testResult.getPercent()) {
            progress.setCompletedTests(progress.getCompletedTests()+1);
            int testCount = course.getModules().stream()
                    .flatMap(module -> module.getLessons().stream())
                    .mapToInt(lesson -> lesson.getTests().size())
                    .sum();
            progress.setCompletionPercentage((double) progress.getCompletedTests() / testCount * 100);
            progressRepository.save(progress);
        }
        return progress.getCompletionPercentage();
    }

    @Transactional
    public long deleteProgressById(Long id) {
        getProgressById(id);
        progressRepository.deleteById(id);
        return id;
    }
}