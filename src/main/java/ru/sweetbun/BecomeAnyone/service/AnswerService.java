package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.CreateAnswerDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateAnswerDTO;
import ru.sweetbun.BecomeAnyone.DTO.toCheck.AnswerToCheckDTO;
import ru.sweetbun.BecomeAnyone.entity.Answer;
import ru.sweetbun.BecomeAnyone.entity.Question;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.AnswerRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final ModelMapper modelMapper;
    private final QuestionService questionService;

    @Autowired
    public AnswerService(AnswerRepository answerRepository, ModelMapper modelMapper, @Lazy QuestionService questionService) {
        this.answerRepository = answerRepository;
        this.modelMapper = modelMapper;
        this.questionService = questionService;
    }

    public Answer createAnswer(CreateAnswerDTO answerDTO, Long questionId) {
        Question question = questionService.getQuestionById(questionId);
        return answerRepository.save(createAnswer(answerDTO, question));
    }

    public void createAnswers(List<CreateAnswerDTO> answerDTOS, Question question) {
        List<Answer> answers = answerDTOS.stream()
                .map(answerDTO -> createAnswer(answerDTO, question))
                .toList();
        answerRepository.saveAll(answers);
    }

    private Answer createAnswer(CreateAnswerDTO answerDTO, Question question) {
        Answer answer = modelMapper.map(answerDTO, Answer.class);
        answer.setQuestion(question);
        question.getAnswers().add(answer);
        return answer;
    }

    public Answer getAnswerById(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Answer.class, id));
    }

    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    public Answer updateAnswer(CreateAnswerDTO answerDTO, Long id) {
        Answer answer = getAnswerById(id);
        modelMapper.map(answerDTO, answer);
        return answerRepository.save(answer);
    }

    public List<Answer> updateAnswers(List<UpdateAnswerDTO> answerDTOS, Question question) {
        Map<Long, Answer> currentAnswersMap = question.getAnswers().stream()
                .collect(Collectors.toMap(Answer::getId, Function.identity()));
        List<Answer> updatedAnswers = new ArrayList<>();

        for (UpdateAnswerDTO answerDTO : answerDTOS) {
            Long answerDTOId = answerDTO.getId();
            if (answerDTOId != null && currentAnswersMap.containsKey(answerDTOId)) {
                Answer answer = currentAnswersMap.get(answerDTOId);
                currentAnswersMap.remove(answerDTOId);
                modelMapper.map(answerDTO, answer);
                updatedAnswers.add(answer);
            } else {
                Answer newAnswer = modelMapper.map(answerDTO, Answer.class);
                newAnswer.setQuestion(question);
                Answer savedAnswer = answerRepository.save(newAnswer);
                updatedAnswers.add(savedAnswer);
            }
        }
        answerRepository.deleteAll(new ArrayList<>(currentAnswersMap.values()));
        return updatedAnswers;
    }

    public String deleteAnswerById(Long id) {
        getAnswerById(id);
        answerRepository.deleteById(id);
        return "Answer has been deleted with id: " + id;
    }

    public boolean checkAnswers(List<AnswerToCheckDTO> answersDTOS, List<Answer> answers) {
        if (answersDTOS.size() != answers.size()) {
            throw new IllegalArgumentException();
        }
        Map<Long, Answer> answerMap = new HashMap<>();
        for (Answer answer : answers) {
            answerMap.put(answer.getId(), answer);
        }
        for (AnswerToCheckDTO answerDTO : answersDTOS) {
            Answer answer = answerMap.get(answerDTO.getId());
            if (answer == null) throw new IllegalArgumentException();
            if (answer.isCorrect() != answerDTO.isCorrect()) return false;
        }
        return true;
    }
}
