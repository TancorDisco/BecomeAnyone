package ru.sweetbun.becomeanyone.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.becomeanyone.dto.answer.request.CreateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.question.request.QuestionRequest;
import ru.sweetbun.becomeanyone.dto.answer.request.UpdateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.question.request.QuestionToCheckRequest;
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.entity.Question;
import ru.sweetbun.becomeanyone.dto.question.response.QuestionResponse;
import ru.sweetbun.becomeanyone.exception.ObjectMustContainException;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.QuestionRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class QuestionServiceImplTests {

    @Mock
    private TestServiceImpl testServiceImpl;

    @Mock
    private AnswerService answerService;

    @Mock
    private QuestionRepository questionRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @InjectMocks
    private QuestionServiceImpl questionServiceImpl;

    private Question question;

    @BeforeEach
    void setUp() {
        questionServiceImpl = new QuestionServiceImpl(testServiceImpl, answerService, questionRepository, modelMapper);
        question = new Question();
    }

    @Test
    void createQuestion_ValidInputs_QuestionCreated() {
        List<CreateAnswerRequest> answers = List.of(CreateAnswerRequest.builder().build());
        QuestionRequest<CreateAnswerRequest> questionRequest = new QuestionRequest<>();
        questionRequest.setAnswers(answers);
        Long testId = 1L;
        ru.sweetbun.becomeanyone.entity.Test test = new ru.sweetbun.becomeanyone.entity.Test();

        when(testServiceImpl.fetchTestById(testId)).thenReturn(test);
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        QuestionResponse result = questionServiceImpl.createQuestion(questionRequest, testId);

        assertNotNull(result);
        verify(questionRepository).save(any(Question.class));
        verify(answerService).createAnswers(answers, question);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void createQuestion_EmptyOrNullAnswers_ThrowsException(List<CreateAnswerRequest> answers) {
        QuestionRequest<CreateAnswerRequest> questionRequest = new QuestionRequest<>();
        questionRequest.setAnswers(answers);

        ObjectMustContainException exception = assertThrows(
                ObjectMustContainException.class,
                () -> questionServiceImpl.createQuestion(questionRequest, 1L)
        );
        assertEquals("Question must contain at least one Answer", exception.getMessage());
    }

    @Test
    void fetchQuestionById_ExistingId_ReturnsQuestion() {
        Long id = 1L;
        when(questionRepository.findById(id)).thenReturn(Optional.of(question));

        Question result = questionServiceImpl.fetchQuestionById(id);

        assertNotNull(result);
        assertEquals(question, result);
    }

    @Test
    void fetchQuestionById_NonExistingId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> questionServiceImpl.fetchQuestionById(id));
    }

    @Test
    void getAllQuestionsByTest_ExistingTest_ReturnsQuestions() {
        Long testId = 1L;
        ru.sweetbun.becomeanyone.entity.Test test = new ru.sweetbun.becomeanyone.entity.Test();
        List<Question> questions = List.of(question);
        when(testServiceImpl.fetchTestById(testId)).thenReturn(test);
        when(questionRepository.findAllQuestionsByTest(test)).thenReturn(questions);

        List<QuestionResponse> result = questionServiceImpl.getAllQuestionsByTest(testId);

        assertNotNull(result);
    }

    @Test
    void updateQuestion_ValidInputs_QuestionUpdated() {
        Long id = 1L;
        QuestionRequest<UpdateAnswerRequest> questionRequest = new QuestionRequest<>();
        List<UpdateAnswerRequest> answers = List.of(UpdateAnswerRequest.builder().build());
        questionRequest.setAnswers(answers);

        when(questionRepository.findById(id)).thenReturn(Optional.of(question));

        QuestionResponse result = questionServiceImpl.updateQuestion(questionRequest, id);

        assertNotNull(result);
        verify(answerService).updateAnswers(answers, question);
    }

    @Test
    void updateQuestion_NonExistingId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        QuestionRequest<UpdateAnswerRequest> questionRequest = new QuestionRequest<>();
        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> questionServiceImpl.updateQuestion(questionRequest, id));
    }

    @Test
    void deleteQuestionById_ExistingId_DeletesQuestion() {
        Long id = 1L;
        when(questionRepository.findById(id)).thenReturn(Optional.of(question));

        long deletedId = questionServiceImpl.deleteQuestionById(id);

        assertEquals(id, deletedId);
        verify(questionRepository).deleteById(id);
    }

    @Test
    void deleteQuestionById_NonExistingId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> questionServiceImpl.deleteQuestionById(id));
    }

    @Test
    void checkQuestions_ValidInputs_ReturnsWrongQuestions() {
        List<QuestionToCheckRequest> questionDTOS = List.of(QuestionToCheckRequest.builder().build());
        List<Question> questions = List.of(question);
        when(answerService.checkAnswers(any(), any())).thenReturn(false);

        List<Question> result = questionServiceImpl.checkQuestions(questionDTOS, questions);

        assertEquals(questions.size(), result.size());
    }

    @Test
    void checkQuestions_QuestionMapMissingQuestion_ThrowsIllegalArgumentException() {
        List<QuestionToCheckRequest> questionDTOS = List.of(QuestionToCheckRequest.builder().id(1L).build());
        List<Question> questions = List.of(Question.builder().id(2L).build());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> questionServiceImpl.checkQuestions(questionDTOS, questions)
        );

        assertEquals("Question with id 1 is null.", exception.getMessage());
    }

    @Test
    void checkQuestions_MismatchedSizes_ThrowsIllegalArgumentException() {
        List<QuestionToCheckRequest> questionDTOS = List.of(QuestionToCheckRequest.builder().build());
        List<Question> questions = List.of(question, question);

        assertThrows(IllegalArgumentException.class, () -> questionServiceImpl.checkQuestions(questionDTOS, questions));
    }
}