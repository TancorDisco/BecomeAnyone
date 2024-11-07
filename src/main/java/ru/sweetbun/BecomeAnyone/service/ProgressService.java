package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.entity.Course;
import ru.sweetbun.BecomeAnyone.entity.Module;
import ru.sweetbun.BecomeAnyone.entity.Progress;
import ru.sweetbun.BecomeAnyone.entity.TestResult;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.ProgressRepository;

import java.util.List;

@Transactional
@Service
public class ProgressService {

    private final ProgressRepository progressRepository;

    private final ModelMapper modelMapper;

    private final double acceptablePercentage;

    @Autowired
    public ProgressService(ProgressRepository progressRepository, ModelMapper modelMapper,
                           @Value("${test-result.percentage.acceptable}") double acceptablePercentage) {
        this.progressRepository = progressRepository;
        this.modelMapper = modelMapper;
        this.acceptablePercentage = acceptablePercentage;
    }

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

    public void updateProgress(TestResult testResult, Course course) {
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
    }

    public void deleteProgressById(Long id) {
        progressRepository.deleteById(id);
    }
}
