package nova.task;

import java.time.LocalDate;

/**
 * A single thing the user wants to keep track of.
 *
 * <p>This is the general case: something with a description that is either
 * done or not. Subclasses add whatever else their kind of task needs, such as
 * a due date, and override {@link #toString} and {@link #toFileString} to
 * include it.
 */
public class Task {
    /** What the user called this task. */
    private final String description;

    /** Whether the user has marked this task as completed. */
    private boolean isDone;

    /**
     * Creates a task that is not done yet.
     *
     * @param description what the user called this task
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Returns the character shown inside the status box.
     *
     * @return {@code X} if the task is done, a space otherwise
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " ");
    }

    /** Marks this task as completed. */
    public void markAsDone() {
        this.isDone = true;
    }

    /** Marks this task as not completed after all. */
    public void markAsNotDone() {
        this.isDone = false;
    }


    /**
     * Returns whether this task's description contains the given keyword,
     * ignoring case.
     *
     * @param keyword the text to search for
     * @return true if the description contains it
     */
    public boolean hasKeyword(String keyword) {
        return description.toLowerCase().contains(keyword.toLowerCase());
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
     * i.e. the done flag followed by the description. Subclasses prepend
     * their type letter and append their own fields.
     *
     * @return the common part of this task's save-file line
     */
    public String toFileString() {
        return (isDone ? "1" : "0") + " | " + description;
    }

    @Override
    public String toString() {
        return "[" + getStatusIcon() + "] " + description;
    }
}
