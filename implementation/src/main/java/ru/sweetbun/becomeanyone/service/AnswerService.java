package ru.sweetbun.becomeanyone.service;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.sweetbun.becomeanyone.dto.answer.request.CreateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.answer.request.UpdateAnswerRequest;
import ru.sweetbun.becomeanyone.dto.answer.request.AnswerToCheckRequest;
import ru.sweetbun.becomeanyone.entity.Answer;
import ru.sweetbun.becomeanyone.entity.Question;
import ru.sweetbun.becomeanyone.exception.ResourceNotFoundException;
import ru.sweetbun.becomeanyone.repository.AnswerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    private final ModelMapper modelMapper;
    @Lazy
    private final QuestionServiceImpl questionServiceImpl;

    @Transactional
    public Answer createAnswer(CreateAnswerRequest answerDTO, Long questionId) {
        Question question = questionServiceImpl.fetchQuestionById(questionId);
        return answerRepository.save(createAnswer(answerDTO, question));
    }

    @Transactional
    public void createAnswers(List<CreateAnswerRequest> answerDTOS, Question question) {
        List<Answer> answers = answerDTOS.stream()
                .map(answerDTO -> createAnswer(answerDTO, question))
                .toList();
        answerRepository.saveAll(answers);
    }

    private Answer createAnswer(CreateAnswerRequest answerDTO, Question question) {
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
    public Answer updateAnswer(CreateAnswerRequest answerDTO, Long id) {
        Answer answer = getAnswerById(id);
        modelMapper.map(answerDTO, answer);
        return answerRepository.save(answer);
    }

    @Transactional
    public List<Answer> updateAnswers(List<UpdateAnswerRequest> answerDTOS, Question question) {
        Map<Long, Answer> currentAnswersMap = question.getAnswers().stream()
                .collect(Collectors.toMap(Answer::getId, Function.identity()));

        List<Answer> updatedAnswers = mergeAnswers(answerDTOS, currentAnswersMap, question);

        if (!currentAnswersMap.isEmpty())
            answerRepository.deleteAll(new ArrayList<>(currentAnswersMap.values()));
        return updatedAnswers;
    }

    private List<Answer> mergeAnswers(List<UpdateAnswerRequest> answerDTOS, Map<Long, Answer> currentAnswersMap,
                                      Question question) {
        return answerDTOS.stream().map(answerDTO -> {
            Long answerDTOId = answerDTO.id();
            Answer answer;

            if (answerDTOId != null && currentAnswersMap.containsKey(answerDTOId)) {
                answer = currentAnswersMap.remove(answerDTOId);
                modelMapper.map(answerDTO, answer);
            } else {
                answer = modelMapper.map(answerDTO, Answer.class);
                answer.setQuestion(question);
            }
            return answer;
        }).toList();
    }

    @Transactional
    public long deleteAnswerById(Long id) {
        getAnswerById(id);
        answerRepository.deleteById(id);
        return id;
    }

    public boolean checkAnswers(List<AnswerToCheckRequest> answersDTOS, List<Answer> answers) {
        validateInputs(answersDTOS, answers);
        Map<Long, Answer> answerMap = answers.stream()
                .collect(Collectors.toMap(Answer::getId, answer -> answer));

        for (AnswerToCheckRequest answerDTO : answersDTOS) {
            Answer answer = answerMap.remove(answerDTO.id());
            if (answer == null) throw new IllegalArgumentException("Incorrect test");
            if (answer.isCorrect() != answerDTO.correct()) return false;
        }
        return true;
    }

    private void validateInputs(List<AnswerToCheckRequest> answerDTOS, List<Answer> answers) {
        if (answerDTOS.size() != answers.size())
            throw new IllegalArgumentException("Sizes of answers lists do not match.");
    }
}
