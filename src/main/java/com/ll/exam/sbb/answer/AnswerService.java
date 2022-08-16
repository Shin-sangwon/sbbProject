package com.ll.exam.sbb.answer;

import com.ll.exam.sbb.question.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Service
public class AnswerService {

    private final AnswerRepository answerRepository;

    public void create(Question question, String content) {
        Answer answer = new Answer();

        question.addAnswer(answer);
        answer.setCreateDate(LocalDateTime.now());
        answer.setContent(content);

        answerRepository.save(answer);
    }
}
