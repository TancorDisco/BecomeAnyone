package ru.sweetbun.BecomeAnyone.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.AnswerDTO;
import ru.sweetbun.BecomeAnyone.DTO.CreateAnswerDTO;
import ru.sweetbun.BecomeAnyone.DTO.QuestionDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateAnswerDTO;
import ru.sweetbun.BecomeAnyone.entity.Answer;
import ru.sweetbun.BecomeAnyone.entity.Question;
import ru.sweetbun.BecomeAnyone.entity.Test;
import ru.sweetbun.BecomeAnyone.exception.ObjectMustContainException;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.QuestionRepository;

import java.util.List;

@Transactional
@Service
public class QuestionService {

    private final TestService testService;

    private final AnswerService answerService;

    private final QuestionRepository questionRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public QuestionService(TestService testService, @Lazy AnswerService answerService, QuestionRepository questionRepository,
                           ModelMapper modelMapper) {
        this.testService = testService;
        this.answerService = answerService;
        this.questionRepository = questionRepository;
        this.modelMapper = modelMapper;
    }

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
                .orElseThrow(() -> new ResourceNotFoundException(Question.class.getSimpleName(), id));
    }

    public List<Question> getAllQuestionsByTest(Long testId) {
        return questionRepository.findAllQuestionsByTest(testService.getTestById(testId));
    }

    public Question updateQuestion(QuestionDTO<UpdateAnswerDTO> questionDTO, Long id) {
        Question question = getQuestionById(id);
        List<UpdateAnswerDTO> answerDTOS = questionDTO.getAnswers();
        validateAnswers(answerDTOS);
        modelMapper.map(questionDTO, question);
        question.setAnswers(answerService.updateAnswers(answerDTOS, question));
        return questionRepository.save(question);
    }

    public String deleteQuestionById(Long id) {
        getQuestionById(id);
        questionRepository.deleteById(id);
        return "Question has been deleted with id: " + id;
    }

    private void validateAnswers(List<? extends AnswerDTO> createAnswerDTOS) {
        if (createAnswerDTOS.isEmpty())
            throw new ObjectMustContainException(Question.class.getSimpleName(), Answer.class.getSimpleName());
    }
}
