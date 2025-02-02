package personal.syaz.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class DateUtils {

    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static void verifyProcessDate(String dateStr) {
        try {
            // If the format is "yyyy-MM", parse it
            LocalDate date = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {

        }
    }

    public static LocalDate toDate(String dateStr) {
        // If the format is "yyyy-MM", parse it
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
