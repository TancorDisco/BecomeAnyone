package ru.sweetbun.becomeanyone.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.aop.AccessControlAspect;
import ru.sweetbun.becomeanyone.config.AuthorizationFilter;
import ru.sweetbun.becomeanyone.dto.answer.request.CreateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.answer.request.UpdateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.question.request.QuestionRequest;
import ru.sweetbun.becomeanyone.entity.Answer;
import ru.sweetbun.becomeanyone.entity.Question;
import ru.sweetbun.becomeanyone.repository.AnswerRepository;
import ru.sweetbun.becomeanyone.repository.QuestionRepository;
import ru.sweetbun.becomeanyone.repository.TestRepository;
import ru.sweetbun.becomeanyone.service.UserServiceImpl;
import ru.sweetbun.becomeanyone.util.SecurityUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class QuestionControllerIntegrationTests extends BaseIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @MockBean
    private SecurityUtils securityUtils;

    @MockBean
    private UserServiceImpl userServiceImpl;

    @MockBean
    private AuthorizationFilter filter;

    @MockBean
    private AccessControlAspect aspect;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ru.sweetbun.becomeanyone.entity.Test savedTest;

    @BeforeEach
    void setUp() {
        savedTest = testRepository.save(
                ru.sweetbun.becomeanyone.entity.Test.builder()
                        .title("Sample Test").questions(new ArrayList<>()).build()
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidQuestionData")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void testCreateQuestionPositive(
            QuestionRequest<CreateAnswerRequest> request,
            Consumer<Question> assertions
    ) throws Exception {
        mockMvc.perform(post("/courses/1/modules/1/lessons/1/tests/"+ savedTest.getId() + "/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Question savedQuestion = questionRepository.findAll().get(0);
        assertions.accept(savedQuestion);
    }

    private static Stream<Arguments> provideValidQuestionData() {
        return Stream.of(
                // 1: правильный вопрос с правильными ответами
                Arguments.of(
                        QuestionRequest.<CreateAnswerRequest>builder()
                                .questionText("What is Java?")
                                .answers(List.of(
                                        CreateAnswerRequest.builder().answerText("A programming language").correct(true).build(),
                                        CreateAnswerRequest.builder().answerText("A coffee").correct(false).build()
                                ))
                                .build(),
                        (Consumer<Question>) question -> {
                            assertNotNull(question);
                            assertEquals("What is Java?", question.getQuestionText());
                            assertEquals(2, question.getAnswers().size());
                        }
                ),

                // 2: вопрос с единственным правильным ответом
                Arguments.of(
                        QuestionRequest.<CreateAnswerRequest>builder()
                                .questionText("What is Java?")
                                .answers(List.of(
                                        CreateAnswerRequest.builder().answerText("A programming language").correct(true).build()
                                ))
                                .build(),
                        (Consumer<Question>) question -> {
                            assertNotNull(question);
                            assertEquals("What is Java?", question.getQuestionText());
                            assertEquals(1, question.getAnswers().size());
                            assertTrue(question.getAnswers().get(0).isCorrect());
                        }
                ),

                // 3: вопрос с множественными правильными ответами
                Arguments.of(
                        QuestionRequest.<CreateAnswerRequest>builder()
                                .questionText("What is Java?")
                                .answers(List.of(
                                        CreateAnswerRequest.builder().answerText("A programming language").correct(true).build(),
                                        CreateAnswerRequest.builder().answerText("A coffee").correct(true).build()
                                ))
                                .build(),
                        (Consumer<Question>) question -> {
                            assertNotNull(question);
                            assertEquals("What is Java?", question.getQuestionText());
                            assertEquals(2, question.getAnswers().size());
                        }
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidQuestionData")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void testCreateQuestionNegative(
            String description,
            QuestionRequest<CreateAnswerRequest> request
    ) throws Exception {
        mockMvc.perform(post("/courses/1/modules/1/lessons/1/tests/"+ savedTest.getId() + "/questions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(description));
    }


    private static Stream<Arguments> provideInvalidQuestionData() {
        return Stream.of(
                // Сценарий: вопрос без правильных ответов
                Arguments.of(
                        "The question must have at least one correct answer",
                        QuestionRequest.<CreateAnswerRequest>builder()
                                .questionText("What is Java?")
                                .answers(List.of(
                                        CreateAnswerRequest.builder().answerText("A programming language").correct(false).build(),
                                        CreateAnswerRequest.builder().answerText("A coffee").correct(false).build()
                                ))
                                .build()
                ),

                // Сценарий: пустой список ответов
                Arguments.of(
                        "Question must contain at least one Answer",
                        QuestionRequest.<CreateAnswerRequest>builder()
                                .questionText("What is Java?")
                                .answers(List.of())
                                .build()
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideValidUpdateQuestionData")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void testUpdateQuestionPositive(
            QuestionRequest<UpdateAnswerRequest> request,
            Consumer<Question> assertions
    ) throws Exception {
        // Сохраняем исходный вопрос с ответами
        Question existingQuestion = questionRepository.save(
                Question.builder()
                        .questionText("Original Question Text")
                        .test(savedTest)
                        .answers(List.of(
                                Answer.builder().answerText("Original Answer 1").correct(true).build(),
                                Answer.builder().answerText("Original Answer 2").correct(false).build()
                        ))
                        .build()
        );
        mockMvc.perform(patch("/courses/1/modules/1/lessons/1/tests/" + savedTest.getId() + "/questions/" + existingQuestion.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        Question updatedQuestion = questionRepository.findById(existingQuestion.getId())
                .orElseThrow(() -> new AssertionError("Updated question not found"));

        assertions.accept(updatedQuestion);
    }

    private static Stream<Arguments> provideValidUpdateQuestionData() {
        return Stream.of(
                // 1: Частичное обновление текста вопроса и замена одного из ответов
                Arguments.of(
                        QuestionRequest.<UpdateAnswerRequest>builder()
                                .questionText("Updated Question Text")
                                .answers(List.of(
                                        UpdateAnswerRequest.builder().id(1L).answerText("Updated Answer 1").correct(true).build(),
                                        UpdateAnswerRequest.builder().id(2L).answerText("Original Answer 2").correct(false).build()
                                ))
                                .build(),
                        (Consumer<Question>) question -> {
                            assertNotNull(question);
                            assertEquals("Updated Question Text", question.getQuestionText());
                            assertEquals(2, question.getAnswers().size());
                            assertEquals("Updated Answer 1", question.getAnswers().get(0).getAnswerText());
                            assertEquals("Original Answer 2", question.getAnswers().get(1).getAnswerText());
                        }
                ),

                // 2: Удаление одного ответа и добавление нового
                Arguments.of(
                        QuestionRequest.<UpdateAnswerRequest>builder()
                                .questionText("Updated Question with Removed and New Answer")
                                .answers(List.of(
                                        UpdateAnswerRequest.builder().id(1L).answerText("Updated Answer 1").correct(true).build(),
                                        UpdateAnswerRequest.builder().id(null).answerText("New Answer 3").correct(false).build()
                                ))
                                .build(),
                        (Consumer<Question>) question -> {
                            assertNotNull(question);
                            assertEquals("Updated Question with Removed and New Answer", question.getQuestionText());
                            assertEquals(2, question.getAnswers().size());
                            assertEquals("Updated Answer 1", question.getAnswers().get(0).getAnswerText());
                            assertEquals("New Answer 3", question.getAnswers().get(1).getAnswerText());
                        }
                ),

                // 3: Полное обновление — замена всех старых ответов
                Arguments.of(
                        QuestionRequest.<UpdateAnswerRequest>builder()
                                .questionText("Question with Completely New Answers")
                                .answers(List.of(
                                        UpdateAnswerRequest.builder().id(null).answerText("Completely New Answer 1").correct(true).build(),
                                        UpdateAnswerRequest.builder().id(null).answerText("Completely New Answer 2").correct(false).build()
                                ))
                                .build(),
                        (Consumer<Question>) question -> {
                            assertNotNull(question);
                            assertEquals("Question with Completely New Answers", question.getQuestionText());
                            assertEquals(2, question.getAnswers().size());
                            assertEquals("Completely New Answer 1", question.getAnswers().get(0).getAnswerText());
                        }
                )
        );
    }


    @ParameterizedTest
    @MethodSource("provideInvalidUpdateQuestionData")
    @WithMockUser(username = "teacher", roles = "TEACHER")
    void testUpdateQuestionNegative(
            String description,
            QuestionRequest<UpdateAnswerRequest> request
    ) throws Exception {
        Question existingQuestion = questionRepository.save(
                Question.builder()
                        .questionText("Old Question Text")
                        .test(savedTest)
                        .answers(new ArrayList<>())
                        .build()
        );

        mockMvc.perform(patch("/courses/1/modules/1/lessons/1/tests/" + savedTest.getId() + "/questions/" + existingQuestion.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(description));
    }

    private static Stream<Arguments> provideInvalidUpdateQuestionData() {
        return Stream.of(
                // 1: нет правильных ответов
                Arguments.of(
                        "The question must have at least one correct answer",
                        QuestionRequest.<UpdateAnswerRequest>builder()
                                .questionText("Text")
                                .answers(List.of(
                                        UpdateAnswerRequest.builder().id(null).answerText("Answer").correct(false).build()
                                ))
                                .build()
                ),
                // 2: нет ответов
                Arguments.of(
                        "Question must contain at least one Answer",
                        QuestionRequest.<UpdateAnswerRequest>builder()
                                .questionText("Updated Question")
                                .answers(List.of())
                                .build()
                )
        );
    }
}
