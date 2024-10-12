package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.AnswerDTO;
import ru.sweetbun.BecomeAnyone.entity.Answer;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.AnswerRepository;

import java.util.List;

@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public AnswerService(AnswerRepository answerRepository, ModelMapper modelMapper) {
        this.answerRepository = answerRepository;
        this.modelMapper = modelMapper;
    }

    public Answer createAnswer(AnswerDTO answerDTO) {
        Answer answer = modelMapper.map(answerDTO, Answer.class);
        return answerRepository.save(answer);
    }

    public Answer getAnswerById(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Answer.class.getSimpleName(), id));
    }

    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    public Answer updateAnswer(AnswerDTO answerDTO, Long id) {
        Answer answer = getAnswerById(id);
        answer = modelMapper.map(answerDTO, Answer.class);
        return answerRepository.save(answer);
    }

    public void deleteAnswerById(Long id) {
        answerRepository.deleteById(id);
    }
}
