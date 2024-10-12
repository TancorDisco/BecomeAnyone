package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.sweetbun.BecomeAnyone.DTO.QuestionDTO;
import ru.sweetbun.BecomeAnyone.entity.Question;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.QuestionRepository;

import java.util.List;

@Service
public class QuestionService {

    private final QuestionRepository questionRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, ModelMapper modelMapper) {
        this.questionRepository = questionRepository;
        this.modelMapper = modelMapper;
    }

    public Question createQuestion(QuestionDTO questionDTO) {
        Question question = modelMapper.map(questionDTO, Question.class);
        return questionRepository.save(question);
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Question.class.getSimpleName(), id));
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question updateQuestion(QuestionDTO questionDTO, Long id) {
        Question question = getQuestionById(id);
        modelMapper.map(questionDTO, question);
        return questionRepository.save(question);
    }

    public void deleteQuestionById(Long id) {
        questionRepository.deleteById(id);
    }
}
