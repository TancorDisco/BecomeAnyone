package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.ProgressDTO;
import ru.sweetbun.BecomeAnyone.entity.Progress;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.ProgressRepository;

import java.util.List;

@Service
public class ProgressService {

    private final ProgressRepository progressRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public ProgressService(ProgressRepository progressRepository, ModelMapper modelMapper) {
        this.progressRepository = progressRepository;
        this.modelMapper = modelMapper;
    }

    public Progress createProgress(ProgressDTO profileDTO) {
        Progress progress = modelMapper.map(profileDTO, Progress.class);
        return progressRepository.save(progress);
    }

    public Progress getProgressById(Long id) {
        return progressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Progress.class.getSimpleName(), id));
    }

    public List<Progress> getAllProgress() {
        return progressRepository.findAll();
    }

    public Progress updateProgress(ProgressDTO progressDTO, Long id) {
        Progress progress = getProgressById(id);
        progress = modelMapper.map(progressDTO, Progress.class);
        return progressRepository.save(progress);
    }

    public void deleteProgressById(Long id) {
        progressRepository.deleteById(id);
    }
}
