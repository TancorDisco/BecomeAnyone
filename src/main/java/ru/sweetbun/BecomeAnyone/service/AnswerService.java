package ru.sweetbun.BecomeAnyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.BecomeAnyone.DTO.CreateAnswerDTO;
import ru.sweetbun.BecomeAnyone.DTO.UpdateAnswerDTO;
import ru.sweetbun.BecomeAnyone.DTO.toCheck.AnswerToCheckDTO;
import ru.sweetbun.BecomeAnyone.entity.Answer;
import ru.sweetbun.BecomeAnyone.entity.Question;
import ru.sweetbun.BecomeAnyone.exception.ResourceNotFoundException;
import ru.sweetbun.BecomeAnyone.repository.AnswerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    private final ModelMapper modelMapper;
    @Lazy
    private final QuestionService questionService;

    @Transactional
    public Answer createAnswer(CreateAnswerDTO answerDTO, Long questionId) {
        Question question = questionService.getQuestionById(questionId);
        return answerRepository.save(createAnswer(answerDTO, question));
    }

    @Transactional
    public void createAnswers(List<CreateAnswerDTO> answerDTOS, Question question) {
        List<Answer> answers = answerDTOS.stream()
                .map(answerDTO -> createAnswer(answerDTO, question))
                .toList();
        answerRepository.saveAll(answers);
    }

    private Answer createAnswer(CreateAnswerDTO answerDTO, Question question) {
        Answer answer = modelMapper.map(answerDTO, Answer.class);
        answer.setQuestion(question);
        question.getAnswers().add(answer);
        return answer;
    }

    public Answer getAnswerById(Long id) {
        return answerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Answer.class, id));
    }

    public List<Answer> getAllAnswers() {
        return answerRepository.findAll();
    }

    @Transactional
    public Answer updateAnswer(CreateAnswerDTO answerDTO, Long id) {
        Answer answer = getAnswerById(id);
        modelMapper.map(answerDTO, answer);
        return answerRepository.save(answer);
    }

    @Transactional
    public List<Answer> updateAnswers(List<UpdateAnswerDTO> answerDTOS, Question question) {
        Map<Long, Answer> currentAnswersMap = question.getAnswers().stream()
                .collect(Collectors.toMap(Answer::getId, Function.identity()));

        List<Answer> updatedAnswers = mergeAnswers(answerDTOS, modelMapper, currentAnswersMap, question);

        if (!currentAnswersMap.isEmpty())
            answerRepository.deleteAll(new ArrayList<>(currentAnswersMap.values()));
        return updatedAnswers;
    }

    @Transactional
    public static List<Answer> mergeAnswers(List<UpdateAnswerDTO> answerDTOS, ModelMapper mapper,
                                            Map<Long, Answer> currentAnswersMap, Question question) {
        return answerDTOS.stream().map(answerDTO -> {
            Long answerDTOId = answerDTO.id();
            Answer answer;

            if (answerDTOId != null && currentAnswersMap.containsKey(answerDTOId)) {
                answer = currentAnswersMap.remove(answerDTOId);
                mapper.map(answerDTO, answer);
            } else {
                answer = mapper.map(answerDTO, Answer.class);
                answer.setQuestion(question);
            }
            return answer;
        }).collect(Collectors.toList());
    }

    @Transactional
    public long deleteAnswerById(Long id) {
        getAnswerById(id);
        answerRepository.deleteById(id);
        return id;
    }

    public boolean checkAnswers(List<AnswerToCheckDTO> answersDTOS, List<Answer> answers) {
        validateInputs(answersDTOS, answers);
        Map<Long, Answer> answerMap = answers.stream()
                .collect(Collectors.toMap(Answer::getId, answer -> answer));

        for (AnswerToCheckDTO answerDTO : answersDTOS) {
            Answer answer = answerMap.remove(answerDTO.id());
            if (answer == null) throw new IllegalArgumentException("Incorrect test");
            if (answer.isCorrect() != answerDTO.correct()) return false;
        }
        return true;
    }

    private void validateInputs(List<AnswerToCheckDTO> answerDTOS, List<Answer> answers) {
        if (answerDTOS.size() != answers.size())
            throw new IllegalArgumentException("Sizes of answers lists do not match.");
    }
}
