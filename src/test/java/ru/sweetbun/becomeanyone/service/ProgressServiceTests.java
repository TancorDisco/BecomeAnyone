package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.sweetbun.becomeanyone.domain.entity.*;
import ru.sweetbun.becomeanyone.domain.entity.Module;
import ru.sweetbun.becomeanyone.domain.service.ProgressService;
import ru.sweetbun.becomeanyone.infrastructure.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.infrastructure.repository.ProgressRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgressServiceTests {

    @Mock
    private ProgressRepository progressRepository;

    private ProgressService progressService;

    private Progress progress;

    @BeforeEach
    void setUp() {
        final double acceptablePercentage = 0.75;
        progressService = new ProgressService(progressRepository, acceptablePercentage);
        progress = new Progress();
    }

    @Test
    void createProgress_NewProgress_CreatedSuccessfully() {
        when(progressRepository.save(any(Progress.class))).thenReturn(progress);

        Progress result = progressService.createProgress();

        assertNotNull(result);
        verify(progressRepository, times(1)).save(any(Progress.class));
    }

    @Test
    void getProgressById_ExistingId_ReturnsProgress() {
        Long id = 1L;
        when(progressRepository.findById(id)).thenReturn(Optional.of(progress));

        Progress result = progressService.getProgressById(id);

        assertNotNull(result);
        assertEquals(progress, result);
    }

    @Test
    void getProgressById_NonExistentId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        when(progressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> progressService.getProgressById(id));
    }

    @Test
    void getAllProgress_MultipleProgress_ReturnsListOfProgress() {
        List<Progress> progressList = List.of(progress, progress);
        when(progressRepository.findAll()).thenReturn(progressList);

        List<Progress> result = progressService.getAllProgress();

        assertEquals(progressList.size(), result.size());
        assertEquals(progressList, result);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.75, 1.0})
    void updateProgress_TestResultAboveAcceptable_ProgressUpdated(double percent) {
        TestResult testResult = TestResult.builder().progress(progress).percent(percent).build();
        progress.setCompletedTests(0);

        ru.sweetbun.becomeanyone.domain.entity.Test test = new ru.sweetbun.becomeanyone.domain.entity.Test();
        Lesson lesson = Lesson.builder().tests(List.of(test, test, test, test)).build();
        Module module = Module.builder().lessons(List.of(lesson)).build();
        Course course = Course.builder().modules(List.of(module)).build();

        double result = progressService.updateProgress(testResult, course);

        assertEquals(25.0, result);
        assertEquals(1, progress.getCompletedTests());
        verify(progressRepository, times(1)).save(progress);
    }

    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.3, 0.6})
    void updateProgress_TestResultBelowAcceptable_ProgressNotUpdated(double percent) {
        TestResult testResult = mock(TestResult.class);
        Course course = mock(Course.class);

        when(testResult.getProgress()).thenReturn(progress);
        when(testResult.getPercent()).thenReturn(percent);

        double result = progressService.updateProgress(testResult, course);

        assertEquals(0.0, result);
        assertEquals(0, progress.getCompletedTests());
        verify(progressRepository, never()).save(progress);
    }

    @Test
    void deleteProgressById_ExistingId_DeletesSuccessfully() {
        Long id = 1L;
        when(progressRepository.findById(id)).thenReturn(Optional.of(progress));

        long deletedId = progressService.deleteProgressById(id);

        assertEquals(id, deletedId);
        verify(progressRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteProgressById_NonExistentId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        when(progressRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> progressService.deleteProgressById(id));
        verify(progressRepository, never()).deleteById(id);
    }
}