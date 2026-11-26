package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        List<Question> questions = questionDao.findAll();
        int number = 1;
        for (Question q : questions) {
            ioService.printFormattedLine("%s. %s", number, q.text());
            char answerPoint = 'a';
            for (Answer a : q.answers()) {
                ioService.printFormattedLine("%s) %s", answerPoint, a.text());
                answerPoint++;
            }
            ioService.printLine("");
            number++;
        }
    }
}
