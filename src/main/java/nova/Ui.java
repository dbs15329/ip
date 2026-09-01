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
        addLines(" Hello! I'm Nova",
                " What can I do for you?");
    }

    /** Says goodbye just before the chatbot exits. */
    public void showGoodbye() {
        addLines(" Bye. Hope to see you again soon!");
    }

    /**
     * Reports a problem to the user.
     *
     * @param message the explanation to show
     */
    public void showError(String message) {
        addLines(" OOPS!!! " + message);
    }

    /**
     * Shows the whole task list, numbered from one.
     *
     * @param tasks the tasks to list
     */
    public void showTaskList(List<Task> tasks) {
        addLines(" Here are the tasks in your list:");
        addNumbered(tasks);
    }

    /**
     * Confirms that a task was added.
     *
     * @param task  the task just added
     * @param total the number of tasks now in the list
     */
    public void showAdded(Task task, int total) {
        addLines(" Got it. I've added this task:",
                "   " + task,
                " Now you have " + total + " tasks in the list.");
    }

    /**
     * Confirms that a task was deleted.
     *
     * @param task      the task just removed
     * @param remaining the number of tasks left in the list
     */
    public void showRemoved(Task task, int remaining) {
        addLines(" Noted. I've removed this task:",
                "   " + task,
                " Now you have " + remaining + " tasks in the list.");
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task the task just marked
     */
    public void showMarked(Task task) {
        addLines(" Nice! I've marked this task as done:",
                "   " + task);
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task the task just unmarked
     */
    public void showUnmarked(Task task) {
        addLines(" OK, I've marked this task as not done yet:",
                "   " + task);
    }

    /**
     * Shows the tasks falling on a particular date.
     *
     * @param dateText the date, already formatted for display
     * @param matches  the tasks that fall on that date
     */
    public void showTasksOn(String dateText, List<Task> matches) {
        addLines(" Here is what you have on " + dateText + ":");
        if (matches.isEmpty()) {
            addLines(" Nothing scheduled. Enjoy the day!");
        }
        addNumbered(matches);
    }

    /**
     * Shows the tasks matching a search.
     *
     * @param matches the tasks whose description contains the keyword
     */
    public void showMatchingTasks(List<Task> matches) {
        addLines(" Here are the matching tasks in your list:");
        if (matches.isEmpty()) {
            addLines(" No matching tasks found.");
        }
        addNumbered(matches);
    }

    /**
     * Warns that the save file could not be read in full.
     *
     * @param skipped how many lines had to be discarded
     */
    public void showLoadingError(int skipped) {
        addLines(" Heads up: I skipped " + skipped + " unreadable line(s) in the save file.",
                " The rest of your tasks loaded fine.");
    }

    /** Appends the given tasks, numbered from one. */
    private void addNumbered(List<Task> tasks) {
        for (int i = 0; i < tasks.size(); i++) {
            addLines(" " + (i + 1) + "." + tasks.get(i));
        }
    }

    /**
     * Appends lines to the message being built.
     *
     * @param lines the lines to add, in order
     */
    private void addLines(String... lines) {
        for (String line : lines) {
            buffer.append(line).append(System.lineSeparator());
        }
    }
}
