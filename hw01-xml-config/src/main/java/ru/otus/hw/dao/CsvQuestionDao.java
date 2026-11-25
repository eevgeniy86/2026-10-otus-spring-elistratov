package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {
    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        List<Question> questions;
        ClassLoader classLoader = getClass().getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(fileNameProvider.getTestFileName());
             InputStreamReader streamReader =
                     new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8)) {
            List<QuestionDto> questionDtoList = new CsvToBeanBuilder<QuestionDto>(streamReader)
                    .withType(QuestionDto.class)
                    .withSkipLines(1)
                    .withSeparator(';')
                    .build()
                    .parse();
            questions = questionDtoList.stream().map(QuestionDto::toDomainObject).toList();
        } catch (NullPointerException npe) {
            throw new QuestionReadException("Unable to read questions list from source");
        } catch (Exception e) {
            throw new QuestionReadException(e.getMessage());
        }
        return questions;
    }
}
