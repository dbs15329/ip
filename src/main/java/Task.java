import java.time.LocalDate;

public class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    public void markAsDone() {
        this.isDone = true;
    }

    public void markAsNotDone() {
        this.isDone = false;
    }


    /**
     * Returns whether this task is associated with the given date. A plain
     * task has no date at all, so it never matches; dated subclasses
     * override this.
     *
     * @param date the date to test against
     * @return true if the task falls on that date
     */
    public boolean isOn(LocalDate date) {
        return false;
    }

    /**
     * Returns the part of the save-file line shared by every task type,
     * i.e. the done flag followed by the description.
     */
    public String toFileString() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
