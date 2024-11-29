package ru.sweetbun.becomeanyone.contract;

import ru.sweetbun.becomeanyone.dto.answer.request.CreateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.answer.request.UpdateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.question.request.QuestionRequest;
import ru.sweetbun.becomeanyone.dto.question.response.QuestionResponse;

import java.util.List;

public interface QuestionService {

    QuestionResponse createQuestion(QuestionRequest<CreateAnswerRequest> questionRequest, Long testId);
    List<QuestionResponse> getAllQuestionsByTest(Long testId);
    QuestionResponse getQuestionById(Long id);
    QuestionResponse updateQuestion(QuestionRequest<UpdateAnswerRequest> questionRequest, Long id);
    long deleteQuestionById(Long id);
}
