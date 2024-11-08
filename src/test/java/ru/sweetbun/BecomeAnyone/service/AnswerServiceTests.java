package ru.sweetbun.BecomeAnyone.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import ru.sweetbun.BecomeAnyone.DTO.CreateAnswerDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateAnswerDTO;
import ru.sweetbun.BecomeAnyone.DTO.toCheck.AnswerToCheckDTO;
import ru.sweetbun.BecomeAnyone.config.ModelMapperConfig;
import ru.sweetbun.BecomeAnyone.entity.Answer;
import ru.sweetbun.BecomeAnyone.entity.Question;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.AnswerRepository;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTests {

    @Mock
    private AnswerRepository answerRepository;

    private ModelMapper modelMapper;

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private AnswerService answerService;

    private Question question;
    private Answer answer;
    private CreateAnswerDTO answerDTO;

    @BeforeEach
    void setup() {
        modelMapper = ModelMapperConfig.createConfiguredModelMapper();
        answerService = new AnswerService(answerRepository, modelMapper, questionService);

        question = Question.builder().id(1L).build();
        answer = Answer.builder().id(1L).answerText("Answer").question(question).build();
        question.getAnswers().add(answer);

        answerDTO = CreateAnswerDTO.builder().build();
    }

    @Test
    void createAnswers_ValidAnswerDTOs_SavesAllAnswers() {
        CreateAnswerDTO answerDTO1 = new CreateAnswerDTO("Answer 1", true);
        CreateAnswerDTO answerDTO2 = new CreateAnswerDTO("Answer 2", false);
        List<CreateAnswerDTO> answerDTOS = List.of(answerDTO1, answerDTO2);

        answerService.createAnswers(answerDTOS, question);

        verify(answerRepository).saveAll(any(List.class));
        assertEquals(3, question.getAnswers().size());
        assertTrue(question.getAnswers().stream().anyMatch(a -> a.getAnswerText().equals("Answer 1") && a.isCorrect()));
        assertTrue(question.getAnswers().stream().anyMatch(a -> a.getAnswerText().equals("Answer 2") && !a.isCorrect()));
    }

    @Test
    void createAnswer_QuestionExists_ReturnsSavedAnswer() {
        when(questionService.getQuestionById(1L)).thenReturn(question);
        when(answerRepository.save(any(Answer.class))).thenReturn(answer);

        Answer createdAnswer = answerService.createAnswer(answerDTO, 1L);

        assertEquals(answer, createdAnswer);
        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    void createAnswer_QuestionNotFound_ThrowsResourceNotFoundException() {
        when(questionService.getQuestionById(1L)).thenThrow(new ResourceNotFoundException(Question.class, 1L));

        assertThrows(ResourceNotFoundException.class, () -> answerService.createAnswer(answerDTO, 1L));
        verify(answerRepository, never()).save(any(Answer.class));
    }

    @Test
    void getAnswerById_AnswerExists_ReturnsAnswer() {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        Answer foundAnswer = answerService.getAnswerById(1L);

        assertEquals(answer, foundAnswer);
    }

    @Test
    void getAnswerById_AnswerDoesNotExist_ThrowsResourceNotFoundException() {
        when(answerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> answerService.getAnswerById(1L));
    }

    @Test
    void updateAnswer_AnswerExists_ReturnsUpdatedAnswer() {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));
        when(answerRepository.save(answer)).thenReturn(answer);

        Answer updatedAnswer = answerService.updateAnswer(answerDTO, 1L);

        assertEquals(answer, updatedAnswer);
        verify(answerRepository).save(answer);
    }

    @Test
    void updateAnswer_AnswerDoesNotExist_ThrowsResourceNotFoundException() {
        when(answerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> answerService.updateAnswer(answerDTO, 1L));
    }

    @Test
    void deleteAnswerById_AnswerExists_ReturnsDeletedAnswerId() {
        when(answerRepository.findById(1L)).thenReturn(Optional.of(answer));

        long deletedAnswerId = answerService.deleteAnswerById(1L);

        assertEquals(1L, deletedAnswerId);
        verify(answerRepository).deleteById(1L);
    }

    @Test
    void deleteAnswerById_AnswerDoesNotExist_ThrowsResourceNotFoundException() {
        when(answerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> answerService.deleteAnswerById(1L));
    }

    @Test
    void getAllAnswers_ReturnsAllAnswers() {
        List<Answer> answers = List.of(answer, new Answer());
        when(answerRepository.findAll()).thenReturn(answers);

        List<Answer> result = answerService.getAllAnswers();

        assertEquals(answers, result);
        verify(answerRepository).findAll();
    }

    @ParameterizedTest
    @MethodSource("checkAnswersData")
    void checkAnswers_VariousCases_ReturnsExpectedResult(List<AnswerToCheckDTO> answerDTOs, List<Answer> answers, boolean expectedResult) {
        boolean result = answerService.checkAnswers(answerDTOs, answers);

        assertEquals(expectedResult, result);
    }

    private static Stream<Arguments> checkAnswersData() {
        Answer answer1 = Answer.builder().id(1L).correct(true).build();
        Answer answer2 = Answer.builder().id(2L).correct(false).build();
        Answer answer3 = Answer.builder().id(2L).correct(true).build();

        AnswerToCheckDTO answerDTO1 = new AnswerToCheckDTO(1L, true);
        AnswerToCheckDTO answerDTO2 = new AnswerToCheckDTO(2L, false);

        return Stream.of(
                Arguments.of(List.of(answerDTO1), List.of(answer1), true),
                Arguments.of(List.of(new AnswerToCheckDTO(1L, false)), List.of(answer1), false),
                Arguments.of(List.of(answerDTO1, answerDTO2), List.of(answer1, answer2), true),
                Arguments.of(
                        List.of(answerDTO1, new AnswerToCheckDTO(2L, true)),
                        List.of(answer1, answer3),
                        true)
        );
    }

    @ParameterizedTest
    @MethodSource("invalidCheckAnswersData")
    void checkAnswers_InvalidCases_ThrowsIllegalArgumentException(List<AnswerToCheckDTO> answerDTOs, List<Answer> answers) {
        Assertions.assertThrows(IllegalArgumentException.class, () -> answerService.checkAnswers(answerDTOs, answers));
    }

    private static Stream<Arguments> invalidCheckAnswersData() {
        Answer answer1 = Answer.builder().id(1L).correct(true).build();
        Answer answer2 = Answer.builder().id(2L).correct(false).build();

        AnswerToCheckDTO answerDTO1 = new AnswerToCheckDTO(1L, true);
        AnswerToCheckDTO answerDTO2 = new AnswerToCheckDTO(2L, false);

        return Stream.of(
                Arguments.of(List.of(answerDTO1, answerDTO2), List.of(answer1)),
                Arguments.of(List.of(new AnswerToCheckDTO(3L, true)), List.of(answer1)),
                Arguments.of(List.of(answerDTO2, answerDTO2), List.of(answer1, answer2)),
                Arguments.of(List.of(answerDTO1, answerDTO1), List.of(answer1, answer2), false)
        );
    }

    @Test
    void mergeAnswers_UpdatesExistingAnswerAndAddsNewOne() {
        //Arrange
        Map<Long, Answer> currentAnswersMap = new HashMap<>();
        currentAnswersMap.put(1L, answer);

        UpdateAnswerDTO updateExistingAnswerDTO = new UpdateAnswerDTO(1L, "", false);
        UpdateAnswerDTO newAnswerDTO = new UpdateAnswerDTO(null, "", true);

        //Act
        List<Answer> mergedAnswers = AnswerService.mergeAnswers(List.of(updateExistingAnswerDTO, newAnswerDTO),
                modelMapper, currentAnswersMap, question);

        //Assert
        assertEquals(2, mergedAnswers.size());

        Answer updatedAnswer = mergedAnswers.get(0);
        assertEquals(1L, updatedAnswer.getId());
        assertFalse(updatedAnswer.isCorrect());

        Answer addedAnswer = mergedAnswers.get(1);
        assertNull(addedAnswer.getId());
        assertTrue(addedAnswer.isCorrect());
        assertSame(question, addedAnswer.getQuestion());

        assertTrue(currentAnswersMap.isEmpty());
    }

    @ParameterizedTest
    @MethodSource("provideMergeAnswersTestCases")
    void mergeAnswers_VariousScenarios(List<UpdateAnswerDTO> answerDTOs,
                                       Map<Long, Answer> currentAnswersMap,
                                       int expectedSize,
                                       boolean expectedFirstAnswerCorrect,
                                       Boolean expectedSecondAnswerCorrect,
                                       int expectedSizeForDeletion) {
        // Act
        List<Answer> mergedAnswers = AnswerService.mergeAnswers(answerDTOs, modelMapper, currentAnswersMap, question);

        // Assert
        assertEquals(expectedSize, mergedAnswers.size());

        if (expectedSize > 0) {
            assertEquals(currentAnswersMap.size(), expectedSizeForDeletion);
            assertEquals(expectedFirstAnswerCorrect, mergedAnswers.get(0).isCorrect());
        }

        if (expectedSize > 1 && expectedSecondAnswerCorrect != null) {
            Answer secondAnswer = mergedAnswers.get(1);
            assertEquals(expectedSecondAnswerCorrect, secondAnswer.isCorrect());
            assertSame(question, secondAnswer.getQuestion());
        }
    }

    private static Stream<Arguments> provideMergeAnswersTestCases() {
        Answer existingAnswer = Answer.builder().id(1L).correct(true).build();
        UpdateAnswerDTO answerDTO1 = new UpdateAnswerDTO(null, "", true);
        UpdateAnswerDTO answerDTO2 = new UpdateAnswerDTO(1L, "", false);

        return Stream.of(
                Arguments.of( // 1
                        List.of(answerDTO2),
                        new HashMap<>(Map.of(1L, existingAnswer)),
                        1, false, null, 0
                ),
                Arguments.of( // 2
                        List.of(answerDTO1),
                        new HashMap<>(Map.of(1L, existingAnswer)),
                        1, true, true, 1
                ),
                Arguments.of( // 3
                        List.of(answerDTO2,
                                answerDTO1),
                        new HashMap<>(Map.of(1L, existingAnswer)),
                        2, false, true, 0
                ),
                Arguments.of( // 4
                        List.of(),
                        new HashMap<>(Map.of(1L, existingAnswer)),
                        0, false, null, 1
                ),
                Arguments.of( // 5
                        List.of(new UpdateAnswerDTO(2L, "", true)),
                        new HashMap<>(Map.of(1L, existingAnswer)),
                        1, true, null, 1
                )
        );
    }
}