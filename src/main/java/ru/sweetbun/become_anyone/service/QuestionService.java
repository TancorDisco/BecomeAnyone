package ru.sweetbun.become_anyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.become_anyone.DTO.AnswerDTO;
import ru.sweetbun.become_anyone.DTO.CreateAnswerDTO;
import ru.sweetbun.become_anyone.DTO.QuestionDTO;
import ru.sweetbun.become_anyone.DTO.UpdateAnswerDTO;
import ru.sweetbun.become_anyone.DTO.toCheck.QuestionToCheckDTO;
import ru.sweetbun.become_anyone.entity.Answer;
import ru.sweetbun.become_anyone.entity.Question;
import ru.sweetbun.become_anyone.entity.Test;
import ru.sweetbun.become_anyone.exception.ObjectMustContainException;
import ru.sweetbun.become_anyone.exception.ResourceNotFoundException;
import ru.sweetbun.become_anyone.repository.QuestionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class QuestionService {
    @Lazy
    private final TestService testService;
    @Lazy
    private final AnswerService answerService;

    private final QuestionRepository questionRepository;

    private final ModelMapper modelMapper;

    @Transactional
    public Question createQuestion(QuestionDTO<CreateAnswerDTO> questionDTO, Long testId) {
        Test test = testService.getTestById(testId);
        List<CreateAnswerDTO> answerDTOS = questionDTO.getAnswers();
        validateAnswers(answerDTOS);
        Question question = modelMapper.map(questionDTO, Question.class);
        question.setTest(test);
        test.getQuestions().add(question);
        Question savedQuestion = questionRepository.save(question);
        answerService.createAnswers(answerDTOS, savedQuestion);
        return savedQuestion;
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Question.class, id));
    }

    public List<Question> getAllQuestionsByTest(Long testId) {
        return questionRepository.findAllQuestionsByTest(testService.getTestById(testId));
    }

    @Transactional
    public Question updateQuestion(QuestionDTO<UpdateAnswerDTO> questionDTO, Long id) {
        Question question = getQuestionById(id);
        List<UpdateAnswerDTO> answerDTOS = questionDTO.getAnswers();
        validateAnswers(answerDTOS);
        modelMapper.map(questionDTO, question);
        question.setAnswers(answerService.updateAnswers(answerDTOS, question));
        return questionRepository.save(question);
    }

    @Transactional
    public long deleteQuestionById(Long id) {
        getQuestionById(id);
        questionRepository.deleteById(id);
        return id;
    }

    private void validateAnswers(List<? extends AnswerDTO> createAnswerDTOS) {
        if (createAnswerDTOS == null || createAnswerDTOS.isEmpty())
            throw new ObjectMustContainException(Question.class.getSimpleName(), Answer.class.getSimpleName());
    }

    public List<Question> checkQuestions(List<QuestionToCheckDTO> questionDTOS, List<Question> questions) {
        validateInputs(questionDTOS, questions);
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, question -> question));

        return questionDTOS.stream()
                .map(dto -> getQuestionIfWrong(dto, questionMap))
                .filter(Objects::nonNull)
                .toList();
    }

    private void validateInputs(List<QuestionToCheckDTO> questionDTOS, List<Question> questions) {
        if (questionDTOS.size() != questions.size())
            throw new IllegalArgumentException("Sizes of question lists do not match.");
    }

    private Question getQuestionIfWrong(QuestionToCheckDTO dto, Map<Long, Question> questionMap) {
        Question question = questionMap.get(dto.id());
        if (question == null) {
            throw new IllegalArgumentException("Question with id " + dto.id() + " is null.");
        }
        return answerService.checkAnswers(dto.answers(), question.getAnswers()) ? null : question;
    }
}
