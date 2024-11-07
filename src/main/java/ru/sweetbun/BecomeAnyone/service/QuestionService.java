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
import ru.sweetbun.BecomeAnyone.DTO.toCheck.QuestionToCheckDTO;
import ru.sweetbun.BecomeAnyone.entity.Answer;
import ru.sweetbun.BecomeAnyone.entity.Question;
import ru.sweetbun.BecomeAnyone.entity.Test;
import ru.sweetbun.BecomeAnyone.exception.ObjectMustContainException;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.QuestionRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
public class QuestionService {

    private final TestService testService;

    private final AnswerService answerService;

    private final QuestionRepository questionRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public QuestionService(@Lazy TestService testService, @Lazy AnswerService answerService,
                           QuestionRepository questionRepository, ModelMapper modelMapper) {
        this.testService = testService;
        this.answerService = answerService;
        this.questionRepository = questionRepository;
        this.modelMapper = modelMapper;
    }

    public Question createQuestion(QuestionDTO<CreateAnswerDTO> questionDTO, Long testId) {
        Test test = testService.getTestById(testId);
        List<CreateAnswerDTO> answerDTOS = questionDTO.answers();
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

    public Question updateQuestion(QuestionDTO<UpdateAnswerDTO> questionDTO, Long id) {
        Question question = getQuestionById(id);
        List<UpdateAnswerDTO> answerDTOS = questionDTO.answers();
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

    public List<Question> checkQuestions(List<QuestionToCheckDTO> questionDTOS, List<Question> questions) {
        if (questionDTOS.size() != questions.size()) {
            throw new IllegalArgumentException("Size not equals");
        }
        Map<Long, Question> questionMap = new HashMap<>();
        List<Question> wrongQuestions = new ArrayList<>();
        for (Question question : questions) {
            questionMap.put(question.getId(), question);
        }
        for (QuestionToCheckDTO questionDTO : questionDTOS) {
            Question question = questionMap.get(questionDTO.id());
            if (question == null) throw new IllegalArgumentException("Question is null");
            if (!answerService.checkAnswers(questionDTO.answers(), question.getAnswers())) {
                wrongQuestions.add(question);
            }
        }
        return wrongQuestions;
    }
}
