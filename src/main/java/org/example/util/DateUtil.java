package org.example.util;

import org.example.exception.DateParseException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

import static org.example.util.Constants.*;
import static org.example.util.ErrorConstants.UNSUPPORTED_DATE_FORMAT;

public class DateUtil {

    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().equals(EMPTY_STRING) ||
                dateStr.equalsIgnoreCase(NULL_VALUE)) {
            return LocalDate.now();
        }

        var formatter = new DateTimeFormatterBuilder()
                // ISO formats
                .appendOptional(DateTimeFormatter.ofPattern(ISO_DATE))
                .appendOptional(DateTimeFormatter.ofPattern(ISO_DATE_TIME))
                .appendOptional(DateTimeFormatter.ofPattern(ISO_DATE_TIME_ZONE))
                .appendOptional(DateTimeFormatter.ofPattern(ISO_INSTANT))

                // European formats
                .appendOptional(DateTimeFormatter.ofPattern(EU_DATE))
                .appendOptional(DateTimeFormatter.ofPattern(EU_DATE_SLASH))
                .appendOptional(DateTimeFormatter.ofPattern(EU_DATE_DOT))
                .appendOptional(DateTimeFormatter.ofPattern(EU_DATE_SPACE))

                // US formats
                .appendOptional(DateTimeFormatter.ofPattern(US_DATE))
                .appendOptional(DateTimeFormatter.ofPattern(US_DATE_SLASH))
                .appendOptional(DateTimeFormatter.ofPattern(US_DATE_DOT))
                .appendOptional(DateTimeFormatter.ofPattern(US_DATE_SPACE))

                // Year first formats
                .appendOptional(DateTimeFormatter.ofPattern(YEAR_FIRST_DASH))
                .appendOptional(DateTimeFormatter.ofPattern(YEAR_FIRST_SLASH))
                .appendOptional(DateTimeFormatter.ofPattern(YEAR_FIRST_DOT))
                .appendOptional(DateTimeFormatter.ofPattern(YEAR_FIRST_SPACE))

                // Short year formats
                .appendOptional(DateTimeFormatter.ofPattern(SHORT_YEAR_EU))
                .appendOptional(DateTimeFormatter.ofPattern(SHORT_YEAR_US))
                .appendOptional(DateTimeFormatter.ofPattern(SHORT_YEAR_FIRST))

                // Month name formats
                .appendOptional(DateTimeFormatter.ofPattern(FULL_MONTH_NAME))
                .appendOptional(DateTimeFormatter.ofPattern(SHORT_MONTH_NAME))
                .appendOptional(DateTimeFormatter.ofPattern(FULL_MONTH_NAME_COMMA))
                .appendOptional(DateTimeFormatter.ofPattern(SHORT_MONTH_NAME_COMMA))
                .toFormatter();

        try {
            return LocalDate.parse(dateStr.trim(), formatter);
        } catch (DateTimeParseException e) {
            throw new DateParseException(String.format(UNSUPPORTED_DATE_FORMAT, dateStr));
        }
    }
}
