package nova.task;

import java.time.LocalDate;
import java.time.LocalDateTime;

import nova.DateTimes;

public class Event extends Task {
    protected LocalDateTime from;
    protected LocalDateTime to;

    public Event(String description, LocalDateTime from, LocalDateTime to) {
        super(description);
        this.from = from;
        this.to = to;
    }


    /**
     * Returns whether this event is running on the given date, i.e. the date
     * lies anywhere between its start day and its end day inclusive.
     *
     * @param date the date to test against
     * @return true if the event spans that date
     */
    @Override
    public boolean isOn(LocalDate date) {
        return !date.isBefore(from.toLocalDate()) && !date.isAfter(to.toLocalDate());
    }

    @Override
    public String toFileString() {
        return "E | " + super.toFileString()
                + " | " + DateTimes.toFileString(from)
                + " | " + DateTimes.toFileString(to);
    }

    @Override
    public String toString() {
        return "[E]" + super.toString()
                + " (from: " + DateTimes.format(from)
                + " to: " + DateTimes.format(to) + ")";
    }
}
