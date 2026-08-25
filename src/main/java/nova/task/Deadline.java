package nova.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import nova.DateTimes;

public class Deadline extends Task {
    private final LocalDateTime by;

    public Deadline(String description, LocalDateTime by) {
        super(description);
        this.by = by;
    }


    /**
     * Returns whether this deadline is due on the given date.
     *
     * @param date the date to test against
     * @return true if the deadline falls on that date
     */
    @Override
    public boolean isOn(LocalDate date) {
        return by.toLocalDate().equals(date);
    }

    @Override
    public String toFileString() {
        return "D | " + super.toFileString() + " | " + DateTimes.toFileString(by);
    }

    @Override
    public String toString() {
        return "[D]" + super.toString() + " (by: " + DateTimes.format(by) + ")";
    }
}
