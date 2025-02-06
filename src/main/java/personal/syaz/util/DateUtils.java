package personal.syaz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Optional;

public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static Optional<YearMonth> safelyParseYearMonth(String input) {
        if (input == null || input.isEmpty()) {
            logger.error("Input date is null or empty.");
            return Optional.empty();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        try {
            YearMonth yearMonth = YearMonth.parse(input, formatter);

            logger.info("YearMonth: {}", yearMonth);

            return Optional.of(yearMonth);
        } catch (DateTimeParseException e) {
            logger.error("Invalid date format. Expected yyyy-MM but received: " + input, e);
            return Optional.empty();
        }
    }

    public static LocalDate toDate(String dateStr) {
        // If the format is "yyyy-MM", parse it
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
