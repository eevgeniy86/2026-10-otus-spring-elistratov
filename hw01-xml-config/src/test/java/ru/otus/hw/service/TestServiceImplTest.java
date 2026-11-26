package ru.otus.hw.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.inOrder;

@Scope("test")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(locations = {"classpath:spring-context.xml"})
public class TestServiceImplTest {

    @MockitoBean
    private IOService ioService;

    @MockitoBean
    private QuestionDao questionDao;

    @Autowired
    private TestServiceImpl testService;

    @Test
    public void testBeanExistence() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("/spring-context.xml");
        var testServiceImpl = context.getBean(TestServiceImpl.class);
        assertNotNull(testServiceImpl);
    }

    @Test
    public void testExceptionThrown() {
        var exceptionMessage = "Error";
        var exception = new QuestionReadException(exceptionMessage);

        Mockito.when(questionDao.findAll()).thenThrow(exception);

        assertThatThrownBy(() -> testService.executeTest())
                .isInstanceOf(QuestionReadException.class)
                .hasMessage(exceptionMessage);
    }

    @Test
    public void testQuestionsPrinting() {
        String answer1Text = "Answer 1";
        String answer2Text = "Answer 2";
        String questionText = "Question";
        String headerText = "Please answer the questions below%n";

        Answer answer1 = new Answer(answer1Text, true);
        Answer answer2 = new Answer(answer2Text, false);
        Question testQuestion = new Question(questionText, Arrays.asList(answer1, answer2));

        InOrder inOrder = inOrder(ioService);

        Mockito.when(questionDao.findAll()).thenReturn(List.of(testQuestion));

        testService.executeTest();

        inOrder.verify(ioService).printLine("");
        inOrder.verify(ioService).printFormattedLine(headerText);
        inOrder.verify(ioService).printFormattedLine("%s. %s", 1, questionText);
        inOrder.verify(ioService).printFormattedLine("%s) %s", 'a', answer1Text);
        inOrder.verify(ioService).printFormattedLine("%s) %s", 'b', answer2Text);
        inOrder.verify(ioService).printLine("");
    }
}

