package me.jiangcai.dating.web.converter;

import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * @author CJ
 */
@Component
public class LocalDateFormatter implements Formatter<LocalDate> {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-M-d");

    @Override
    public LocalDate parse(String text, Locale locale) throws ParseException {
        if (text == null)
            return null;
        try {
            return LocalDate.from(dateTimeFormatter.parse(text));
        } catch (DateTimeParseException exception) {
            throw new ParseException(exception.getParsedString(), exception.getErrorIndex());
        }
    }

    @Override
    public String print(LocalDate object, Locale locale) {
        if (object == null)
            return null;
        return dateTimeFormatter.format(object);
    }
}
