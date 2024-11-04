package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.ProgressDTO;
import ru.sweetbun.BecomeAnyone.entity.Progress;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.ProgressRepository;

import java.util.List;

@Transactional
@Service
public class ProgressService {

    private final ProgressRepository progressRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public ProgressService(ProgressRepository progressRepository, ModelMapper modelMapper) {
        this.progressRepository = progressRepository;
        this.modelMapper = modelMapper;
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

    public Progress updateProgress(ProgressDTO progressDTO, Long id) {
        Progress progress = getProgressById(id);
        modelMapper.map(progressDTO, progress);
        return progressRepository.save(progress);
    }

    public void deleteProgressById(Long id) {
        progressRepository.deleteById(id);
    }
}
