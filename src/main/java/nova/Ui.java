package nova;

import java.util.List;
import java.util.Scanner;

import nova.task.Task;

/**
 * Builds everything the chatbot says to the user, and reads what they type
 * back.
 *
 * <p>Messages are collected into a buffer rather than printed. Whoever is
 * driving the chatbot calls {@link #flush()} once the current command is done
 * and decides how to present the text: the console front end frames it with
 * divider lines, while the GUI puts it in a dialog bubble. That is what lets
 * both front ends share one set of messages.
 */
public class Ui {
    private final Scanner scanner = new Scanner(System.in);
    private final StringBuilder buffer = new StringBuilder();

    /** Creates a user interface that reads from the console. */
    public Ui() {
    }

    /**
     * Reads the next command typed by the user.
     *
     * @return the trimmed line of input
     */
    public String readCommand() {
        return scanner.nextLine().trim();
    }

    /** Releases the input stream once the chatbot is shutting down. */
    public void close() {
        scanner.close();
    }

    /**
     * Returns everything said since the last call, and empties the buffer.
     *
     * @return the accumulated message, without a trailing line separator
     */
    public String flush() {
        String message = buffer.toString();
        buffer.setLength(0);
        return message.stripTrailing();
    }

    /** Greets the user on startup. */
    public void showWelcome() {
        addLine(" Hello! I'm Nova");
        addLine(" What can I do for you?");
    }

    /** Says goodbye just before the chatbot exits. */
    public void showGoodbye() {
        addLine(" Bye. Hope to see you again soon!");
    }

    /**
     * Reports a problem to the user.
     *
     * @param message the explanation to show
     */
    public void showError(String message) {
        addLine(" OOPS!!! " + message);
    }

    /**
     * Shows the whole task list, numbered from one.
     *
     * @param tasks the tasks to list
     */
    public void showTaskList(List<Task> tasks) {
        addLine(" Here are the tasks in your list:");
        addNumbered(tasks);
    }

    /**
     * Confirms that a task was added.
     *
     * @param task  the task just added
     * @param total the number of tasks now in the list
     */
    public void showAdded(Task task, int total) {
        addLine(" Got it. I've added this task:");
        addLine("   " + task);
        addLine(" Now you have " + total + " tasks in the list.");
    }

    /**
     * Confirms that a task was deleted.
     *
     * @param task      the task just removed
     * @param remaining the number of tasks left in the list
     */
    public void showRemoved(Task task, int remaining) {
        addLine(" Noted. I've removed this task:");
        addLine("   " + task);
        addLine(" Now you have " + remaining + " tasks in the list.");
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task the task just marked
     */
    public void showMarked(Task task) {
        addLine(" Nice! I've marked this task as done:");
        addLine("   " + task);
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task the task just unmarked
     */
    public void showUnmarked(Task task) {
        addLine(" OK, I've marked this task as not done yet:");
        addLine("   " + task);
    }

    /**
     * Shows the tasks falling on a particular date.
     *
     * @param dateText the date, already formatted for display
     * @param matches  the tasks that fall on that date
     */
    public void showTasksOn(String dateText, List<Task> matches) {
        addLine(" Here is what you have on " + dateText + ":");
        if (matches.isEmpty()) {
            addLine(" Nothing scheduled. Enjoy the day!");
        }
        addNumbered(matches);
    }

    /**
     * Shows the tasks matching a search.
     *
     * @param matches the tasks whose description contains the keyword
     */
    public void showMatchingTasks(List<Task> matches) {
        addLine(" Here are the matching tasks in your list:");
        if (matches.isEmpty()) {
            addLine(" No matching tasks found.");
        }
        addNumbered(matches);
    }

    /**
     * Warns that the save file could not be read in full.
     *
     * @param skipped how many lines had to be discarded
     */
    public void showLoadingError(int skipped) {
        addLine(" Heads up: I skipped " + skipped + " unreadable line(s) in the save file.");
        addLine(" The rest of your tasks loaded fine.");
    }

    /** Appends the given tasks, numbered from one. */
    private void addNumbered(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            addLine(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /** Appends one line to the message being built. */
    private void addLine(String line) {
        buffer.append(line).append(System.lineSeparator());
    }
}
