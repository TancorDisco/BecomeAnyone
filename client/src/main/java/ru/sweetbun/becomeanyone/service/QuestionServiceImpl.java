package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sweetbun.becomeanyone.contract.QuestionService;
import ru.sweetbun.becomeanyone.dto.answer.request.CreateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.answer.request.UpdateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.question.request.QuestionRequest;
import ru.sweetbun.becomeanyone.dto.question.response.QuestionResponse;
import ru.sweetbun.becomeanyone.feign.QuestionServiceClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionServiceClient questionServiceClient;

    @Override
    public QuestionResponse createQuestion(QuestionRequest<CreateAnswerRequest> questionRequest, Long testId) {
        return questionServiceClient.createQuestion(questionRequest, testId);
    }

    @Override
    public List<QuestionResponse> getAllQuestionsByTest(Long testId) {
        return questionServiceClient.getAllQuestionsByTest(testId);
    }

    @Override
    public QuestionResponse getQuestionById(Long id) {
        return questionServiceClient.getQuestionById(id);
    }

    @Override
    public QuestionResponse updateQuestion(QuestionRequest<UpdateAnswerRequest> questionRequest, Long id) {
        return questionServiceClient.updateQuestion(questionRequest, id);
    }

    @Override
    public long deleteQuestionById(Long id) {
        return questionServiceClient.deleteQuestionById(id);
    }
}
