package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.contract.QuestionService;
import ru.sweetbun.becomeanyone.dto.answer.request.AnswerRequest;
import ru.sweetbun.becomeanyone.dto.answer.request.CreateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.question.request.QuestionRequest;
import ru.sweetbun.becomeanyone.dto.answer.request.UpdateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.question.request.QuestionToCheckRequest;
import ru.sweetbun.becomeanyone.entity.Answer;
import ru.sweetbun.becomeanyone.entity.Question;
import ru.sweetbun.becomeanyone.entity.Test;
import ru.sweetbun.becomeanyone.dto.question.response.QuestionResponse;
import ru.sweetbun.becomeanyone.exception.ObjectMustContainException;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.QuestionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class QuestionServiceImpl implements QuestionService {
    @Lazy
    private final TestServiceImpl testServiceImpl;
    @Lazy
    private final AnswerService answerService;

    private final QuestionRepository questionRepository;

    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public QuestionResponse createQuestion(QuestionRequest<CreateAnswerRequest> questionRequest, Long testId) {
        Test test = testServiceImpl.fetchTestById(testId);
        List<CreateAnswerRequest> answerDTOS = questionRequest.getAnswers();
        validateAnswers(answerDTOS);
        Question question = modelMapper.map(questionRequest, Question.class);
        question.setTest(test);
        test.getQuestions().add(question);
        Question savedQuestion = questionRepository.save(question);
        answerService.createAnswers(answerDTOS, savedQuestion);
        return modelMapper.map(savedQuestion, QuestionResponse.class);
    }

    @Override
    public QuestionResponse getQuestionById(Long id) {
        Question question = fetchQuestionById(id);
        return modelMapper.map(question, QuestionResponse.class);
    }

    public Question fetchQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Question.class, id));
    }

    @Override
    public List<QuestionResponse> getAllQuestionsByTest(Long testId) {
        return questionRepository.findAllQuestionsByTest(testServiceImpl.fetchTestById(testId)).stream()
                .map(question -> modelMapper.map(question, QuestionResponse.class))
                .toList();
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(QuestionRequest<UpdateAnswerRequest> questionRequest, Long id) {
        Question question = fetchQuestionById(id);
        List<UpdateAnswerRequest> answerDTOS = questionRequest.getAnswers();
        validateAnswers(answerDTOS);
        modelMapper.map(questionRequest, question);
        question.setAnswers(answerService.updateAnswers(answerDTOS, question));
        Question savedQuestion = questionRepository.save(question);
        return modelMapper.map(savedQuestion, QuestionResponse.class);
    }

    @Override
    @Transactional
    public long deleteQuestionById(Long id) {
        fetchQuestionById(id);
        questionRepository.deleteById(id);
        return id;
    }

    private void validateAnswers(List<? extends AnswerRequest> createAnswerDTOS) {
        if (createAnswerDTOS == null || createAnswerDTOS.isEmpty())
            throw new ObjectMustContainException(Question.class.getSimpleName(), Answer.class.getSimpleName());
    }

    public List<Question> checkQuestions(List<QuestionToCheckRequest> questionDTOS, List<Question> questions) {
        validateInputs(questionDTOS, questions);
        Map<Long, Question> questionMap = questions.stream()
                .collect(Collectors.toMap(Question::getId, question -> question));

        return questionDTOS.stream()
                .map(dto -> getQuestionIfWrong(dto, questionMap))
                .filter(Objects::nonNull)
                .toList();
    }

    private void validateInputs(List<QuestionToCheckRequest> questionDTOS, List<Question> questions) {
        if (questionDTOS.size() != questions.size())
            throw new IllegalArgumentException("Sizes of question lists do not match.");
    }

    private Question getQuestionIfWrong(QuestionToCheckRequest dto, Map<Long, Question> questionMap) {
        Question question = questionMap.get(dto.id());
        if (question == null) {
            throw new IllegalArgumentException("Question with id " + dto.id() + " is null.");
        }
        return answerService.checkAnswers(dto.answers(), question.getAnswers()) ? null : question;
    }
}
