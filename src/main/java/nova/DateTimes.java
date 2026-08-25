package nova;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Converts between the date-time text a user types, the text shown back to
 * them, and the text stored in the save file.
 *
 * <p>A task's time is always kept as a {@link LocalDateTime}. When the user
 * gives a date with no time of day, midnight is stored and the time is left
 * out when displaying, so "2019-12-02" reads back as "Dec 02 2019" rather
 * than "Dec 02 2019, 12:00 am".
 */
public final class DateTimes {

    /** Input formats accepted for a date plus a time of day. */
    private static final List<DateTimeFormatter> DATE_TIME_FORMATS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd HHmm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HHmm"),
            DateTimeFormatter.ofPattern("d/M/yyyy HH:mm"),
            DateTimeFormatter.ISO_LOCAL_DATE_TIME);

    /** Input formats accepted for a bare date, which is read as midnight. */
    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
            DateTimeFormatter.ofPattern("yyyy-MM-dd"),
            DateTimeFormatter.ofPattern("d/M/yyyy"));

    private static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("MMM dd yyyy");
    private static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("MMM dd yyyy, h:mma");

    /** Utility class: not meant to be instantiated. */
    private DateTimes() {
    }

    /**
     * Parses user input into a date and time.
     *
     * @param input text such as {@code 2019-12-02 1800} or {@code 2/12/2019}
     * @return the parsed date and time, at midnight if no time was given
     * @throws NovaException if the text matches none of the accepted formats
     */
    public static LocalDateTime parse(String input) throws NovaException {
        String trimmed = input.trim();

        for (DateTimeFormatter format : DATE_TIME_FORMATS) {
            try {
                return LocalDateTime.parse(trimmed, format);
            } catch (DateTimeParseException e) {
                // Try the next format.
            }
        }

        for (DateTimeFormatter format : DATE_FORMATS) {
            try {
                return LocalDate.parse(trimmed, format).atStartOfDay();
            } catch (DateTimeParseException e) {
                // Try the next format.
            }
        }

        throw new NovaException("I couldn't read '" + trimmed + "' as a date. "
                + "Try yyyy-MM-dd or yyyy-MM-dd HHmm, e.g. 2019-12-02 1800.");
    }

    /**
     * Parses user input into a plain date, ignoring any time of day that was
     * supplied.
     *
     * @param input text such as {@code 2019-12-02} or {@code 2/12/2019}
     * @return the parsed date
     * @throws NovaException if the text matches none of the accepted formats
     */
    public static LocalDate parseDate(String input) throws NovaException {
        return parse(input).toLocalDate();
    }

    /**
     * Formats a plain date for display.
     *
     * @param date the value to format
     * @return text such as {@code Dec 02 2019}
     */
    public static String formatDate(LocalDate date) {
        return date.format(DISPLAY_DATE);
    }

    /**
     * Formats a date and time for display, omitting the time when it is
     * midnight.
     *
     * @param dateTime the value to format
     * @return text such as {@code Dec 02 2019} or {@code Dec 02 2019, 6:00pm}
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return dateTime.format(DISPLAY_DATE);
        }
        return dateTime.format(DISPLAY_DATE_TIME).replace("AM", "am").replace("PM", "pm");
    }

    /**
     * Formats a date and time for the save file, using ISO-8601 so that it
     * round-trips exactly.
     *
     * @param dateTime the value to format
     * @return text such as {@code 2019-12-02T18:00}
     */
    public static String toFileString(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
