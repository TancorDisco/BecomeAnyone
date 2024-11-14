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
import ru.sweetbun.becomeanyone.dto.CreateAnswerDTO;
import ru.sweetbun.becomeanyone.dto.QuestionDTO;
import ru.sweetbun.becomeanyone.dto.UpdateAnswerDTO;
import ru.sweetbun.becomeanyone.dto.toCheck.QuestionToCheckDTO;
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.entity.Question;
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
class QuestionServiceTests {

    @Mock
    private TestService testService;

    @Mock
    private AnswerService answerService;

    @Mock
    private QuestionRepository questionRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @InjectMocks
    private QuestionService questionService;

    private Question question;

    @BeforeEach
    void setUp() {
        questionService = new QuestionService(testService, answerService, questionRepository, modelMapper);
        question = new Question();
    }

    @Test
    void createQuestion_ValidInputs_QuestionCreated() {
        List<CreateAnswerDTO> answers = List.of(CreateAnswerDTO.builder().build());
        QuestionDTO<CreateAnswerDTO> questionDTO = new QuestionDTO<>();
        questionDTO.setAnswers(answers);
        Long testId = 1L;
        ru.sweetbun.becomeanyone.entity.Test test = new ru.sweetbun.becomeanyone.entity.Test();

        when(testService.getTestById(testId)).thenReturn(test);
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        Question result = questionService.createQuestion(questionDTO, testId);

        assertNotNull(result);
        verify(questionRepository).save(any(Question.class));
        verify(answerService).createAnswers(answers, question);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void createQuestion_EmptyOrNullAnswers_ThrowsException(List<CreateAnswerDTO> answers) {
        QuestionDTO<CreateAnswerDTO> questionDTO = new QuestionDTO<>();
        questionDTO.setAnswers(answers);

        ObjectMustContainException exception = assertThrows(
                ObjectMustContainException.class,
                () -> questionService.createQuestion(questionDTO, 1L)
        );
        assertEquals("Question must contain at least one Answer", exception.getMessage());
    }

    @Test
    void getQuestionById_ExistingId_ReturnsQuestion() {
        Long id = 1L;
        when(questionRepository.findById(id)).thenReturn(Optional.of(question));

        Question result = questionService.getQuestionById(id);

        assertNotNull(result);
        assertEquals(question, result);
    }

    @Test
    void getQuestionById_NonExistingId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> questionService.getQuestionById(id));
    }

    @Test
    void getAllQuestionsByTest_ExistingTest_ReturnsQuestions() {
        Long testId = 1L;
        ru.sweetbun.becomeanyone.entity.Test test = new ru.sweetbun.becomeanyone.entity.Test();
        List<Question> questions = List.of(question);
        when(testService.getTestById(testId)).thenReturn(test);
        when(questionRepository.findAllQuestionsByTest(test)).thenReturn(questions);

        List<Question> result = questionService.getAllQuestionsByTest(testId);

        assertEquals(questions, result);
    }

    @Test
    void updateQuestion_ValidInputs_QuestionUpdated() {
        Long id = 1L;
        QuestionDTO<UpdateAnswerDTO> questionDTO = new QuestionDTO<>();
        List<UpdateAnswerDTO> answers = List.of(UpdateAnswerDTO.builder().build());
        questionDTO.setAnswers(answers);

        when(questionRepository.findById(id)).thenReturn(Optional.of(question));
        when(questionRepository.save(question)).thenReturn(question);

        Question result = questionService.updateQuestion(questionDTO, id);

        assertNotNull(result);
        verify(answerService).updateAnswers(answers, question);
        verify(questionRepository).save(question);
    }

    @Test
    void updateQuestion_NonExistingId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        QuestionDTO<UpdateAnswerDTO> questionDTO = new QuestionDTO<>();
        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> questionService.updateQuestion(questionDTO, id));
    }

    @Test
    void deleteQuestionById_ExistingId_DeletesQuestion() {
        Long id = 1L;
        when(questionRepository.findById(id)).thenReturn(Optional.of(question));

        long deletedId = questionService.deleteQuestionById(id);

        assertEquals(id, deletedId);
        verify(questionRepository).deleteById(id);
    }

    @Test
    void deleteQuestionById_NonExistingId_ThrowsResourceNotFoundException() {
        Long id = 1L;
        when(questionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> questionService.deleteQuestionById(id));
    }

    @Test
    void checkQuestions_ValidInputs_ReturnsWrongQuestions() {
        List<QuestionToCheckDTO> questionDTOS = List.of(QuestionToCheckDTO.builder().build());
        List<Question> questions = List.of(question);
        when(answerService.checkAnswers(any(), any())).thenReturn(false);

        List<Question> result = questionService.checkQuestions(questionDTOS, questions);

        assertEquals(questions.size(), result.size());
    }

    @Test
    void checkQuestions_QuestionMapMissingQuestion_ThrowsIllegalArgumentException() {
        List<QuestionToCheckDTO> questionDTOS = List.of(QuestionToCheckDTO.builder().id(1L).build());
        List<Question> questions = List.of(Question.builder().id(2L).build());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> questionService.checkQuestions(questionDTOS, questions)
        );

        assertEquals("Question with id 1 is null.", exception.getMessage());
    }

    @Test
    void checkQuestions_MismatchedSizes_ThrowsIllegalArgumentException() {
        List<QuestionToCheckDTO> questionDTOS = List.of(QuestionToCheckDTO.builder().build());
        List<Question> questions = List.of(question, question);

        assertThrows(IllegalArgumentException.class, () -> questionService.checkQuestions(questionDTOS, questions));
    }
}