package ru.sweetbun.becomeanyone.service;

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
import ru.sweetbun.becomeanyone.dto.answer.request.CreateAnswerDTO;
import ru.sweetbun.becomeanyone.dto.answer.request.UpdateAnswerDTO;
import ru.sweetbun.becomeanyone.dto.answer.request.AnswerToCheckDTO;
import ru.sweetbun.becomeanyone.config.ModelMapperConfig;
import ru.sweetbun.becomeanyone.domain.entity.Answer;
import ru.sweetbun.becomeanyone.domain.entity.Question;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.domain.repository.AnswerRepository;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTests {

    @Mock
    private AnswerRepository answerRepository;

    private final ModelMapper modelMapper = ModelMapperConfig.createConfiguredModelMapper();

    @Mock
    private QuestionService questionService;

    @InjectMocks
    private AnswerService answerService;

    private Question question;
    private Answer answer;
    private CreateAnswerDTO answerDTO;

    @BeforeEach
    void setup() {
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
    void updateAnswers_ValidAnswerDTOs_CallsDeleteAllAndSaveAll() {
        // Arrange
        List<UpdateAnswerDTO> updateAnswerDTOS = List.of(
                new UpdateAnswerDTO(1L, "Updated Answer 1", false),
                new UpdateAnswerDTO(null, "New Answer", true));

        // Act
        List<Answer> result = answerService.updateAnswers(updateAnswerDTOS, question);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(answerRepository, never()).deleteAll(any());
        assertEquals("Updated Answer 1", result.get(0).getAnswerText());
        assertFalse(result.get(0).isCorrect());
        assertEquals("New Answer", result.get(1).getAnswerText());
        assertTrue(result.get(1).isCorrect());
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

    @ParameterizedTest
    @MethodSource("provideTestData")
    void testUpdateAnswers(List<UpdateAnswerDTO> answerDTOS, List<Answer> expectedAnswers) {
        // Act
        List<Answer> updatedAnswers = answerService.updateAnswers(answerDTOS, question);

        // Assert
        assertEquals(expectedAnswers.size(), updatedAnswers.size());
        for (int i = 0; i < expectedAnswers.size(); i++) {
            assertEquals(expectedAnswers.get(i).getAnswerText(), updatedAnswers.get(i).getAnswerText());
            assertEquals(expectedAnswers.get(i).isCorrect(), updatedAnswers.get(i).isCorrect());
        }
    }

    private static Stream<Arguments> provideTestData() {
        return Stream.of(
                // 1: Add new answers
                Arguments.of(
                        List.of(
                                UpdateAnswerDTO.builder().id(1L).answerText("Answer").correct(false).build(),
                                UpdateAnswerDTO.builder().answerText("New Answer 1").correct(true).build(),
                                UpdateAnswerDTO.builder().answerText("New Answer 2").correct(false).build()
                        ),
                        List.of(
                                Answer.builder().id(1L).answerText("Answer").correct(false).build(),
                                Answer.builder().id(2L).answerText("New Answer 1").correct(true).build(),
                                Answer.builder().id(3L).answerText("New Answer 2").correct(false).build()
                        )
                ),
                // 2: Update existing answers
                Arguments.of(
                        List.of(
                                UpdateAnswerDTO.builder().id(1L).answerText("Updated Answer 1").correct(false).build()
                        ),
                        List.of(
                                Answer.builder().id(1L).answerText("Updated Answer 1").correct(false).build()
                        )
                ),
                // 3: Add new and update existing answers
                Arguments.of(
                        List.of(
                                UpdateAnswerDTO.builder().id(1L).answerText("Updated Answer 1").correct(false).build(),
                                UpdateAnswerDTO.builder().answerText("New Answer 3").correct(true).build()
                        ),
                        List.of(
                                Answer.builder().id(1L).answerText("Updated Answer 1").correct(false).build(),
                                Answer.builder().id(2L).answerText("New Answer 3").correct(true).build()
                        )
                ),
                // 4: Add new answers & delete old
                Arguments.of(
                        List.of(
                                UpdateAnswerDTO.builder().answerText("New Answer 1").correct(true).build(),
                                UpdateAnswerDTO.builder().answerText("New Answer 2").correct(false).build()
                        ),
                        List.of(
                                Answer.builder().id(2L).answerText("New Answer 1").correct(true).build(),
                                Answer.builder().id(3L).answerText("New Answer 2").correct(false).build()
                        )
                )
        );
    }
}