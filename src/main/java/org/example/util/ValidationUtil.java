package org.example.util;

import lombok.extern.log4j.Log4j2;
import org.example.exception.CsvValidationException;
import org.example.exception.DateParseException;
import org.example.model.EmployeeWorkDuration;

import java.util.Set;
import java.util.regex.Pattern;

import static org.example.util.LoggingConstants.*;

@Log4j2
public class ValidationUtil {
    public static final Pattern DIGIT_PATTERN = Pattern.compile("\\d+");


    public static void validateHeader(String headerLine) {
        if (headerLine == null) {
            throw new CsvValidationException(CSV_EMPTY);
        }
        if (!headerLine.contains(COMMA_DELIMITER)) {
            throw new CsvValidationException(INVALID_CSV_FORMAT);
        }

        var headers = headerLine.split(COMMA_DELIMITER);
        if (headers.length < EXPECTED_COLUMN_COUNT) {
            throw new CsvValidationException(
                    String.format(INVALID_HEADER, EXPECTED_COLUMN_COUNT)
            );
        }
    }

    public static void validateResults(Set<EmployeeWorkDuration> records) {
        if (records.isEmpty()) {
            throw new CsvValidationException(NO_VALID_RECORDS);
        }
    }

    public static EmployeeWorkDuration parseEmployeeWorkDuration(String[] parts, int lineNumber) {
        try {
            // Validate and parse employee ID
            var empIdStr = parts[0].trim();
            if (!DIGIT_PATTERN.matcher(empIdStr).matches()) {
                log.warn(INVALID_EMPLOYEE_ID, lineNumber, empIdStr);
                return null;
            }
            var empId = Integer.parseInt(empIdStr);

            // Validate and parse project ID
            var projectIdStr = parts[1].trim();
            if (!DIGIT_PATTERN.matcher(projectIdStr).matches()) {
                log.warn(INVALID_PROJECT_ID, lineNumber, projectIdStr);
                return null;
            }
            var projectId = Integer.parseInt(projectIdStr);

            // Parse dates with validation
            var dateFrom = DateUtil.parseDate(parts[2].trim());
            if (dateFrom == null) {
                log.warn(INVALID_DATE_FROM, lineNumber, parts[2]);
                return null;
            }

            var dateTo = DateUtil.parseDate(parts[3].trim());
            if (dateTo == null) {
                log.warn(INVALID_DATE_TO, lineNumber, parts[3]);
                return null;
            }

            // Validate date range
            if (dateFrom.isAfter(dateTo)) {
                log.warn(INVALID_DATE_RANGE, lineNumber, dateFrom, dateTo);
                return null;
            }

            return new EmployeeWorkDuration(empId, projectId, dateFrom, dateTo);

        } catch (NumberFormatException e) {
            log.error(NUMBER_PARSING_ERROR, lineNumber, e.getMessage());
            return null;
        } catch (DateParseException e) {
            log.error(DATE_PARSING_ERROR, lineNumber, e.getMessage());
            return null;
        }
    }
}
