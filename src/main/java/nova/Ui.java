package nova;

import java.util.List;
import java.util.Scanner;

import nova.task.Task;

/**
 * Handles everything the chatbot shows the user and everything it reads back.
 *
 * <p>Keeping console input and output in one place means the rest of the code
 * never touches {@code System.out} directly, so the wording of a message can
 * be changed, or the console swapped for another interface, without touching
 * the task-handling logic.
 */
public class Ui {
    private static final String LINE = "____________________________________________________________";

    private final Scanner scanner = new Scanner(System.in);

    /** Creates a user interface that reads from and writes to the console. */
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

    /** Prints the divider line used to frame every block of output. */
    public void showLine() {
        System.out.println(LINE);
    }

    /** Greets the user on startup. */
    public void showWelcome() {
        showBlock(" Hello! I'm Nova", " What can I do for you?");
    }

    /** Says goodbye just before the chatbot exits. */
    public void showGoodbye() {
        showBlock(" Bye. Hope to see you again soon!");
    }

    /**
     * Reports a problem to the user.
     *
     * @param message the explanation to show
     */
    public void showError(String message) {
        showBlock(" OOPS!!! " + message);
    }

    /**
     * Shows the whole task list, numbered from one.
     *
     * @param tasks the tasks to list
     */
    public void showTaskList(List<Task> tasks) {
        showLine();
        System.out.println(" Here are the tasks in your list:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println(" " + (i + 1) + "." + tasks.get(i));
        }
        showLine();
    }

    /**
     * Confirms that a task was added.
     *
     * @param task  the task just added
     * @param total the number of tasks now in the list
     */
    public void showAdded(Task task, int total) {
        showBlock(" Got it. I've added this task:",
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
        showBlock(" Noted. I've removed this task:",
                "   " + task,
                " Now you have " + remaining + " tasks in the list.");
    }

    /**
     * Confirms that a task was marked as done.
     *
     * @param task the task just marked
     */
    public void showMarked(Task task) {
        showBlock(" Nice! I've marked this task as done:", "   " + task);
    }

    /**
     * Confirms that a task was marked as not done.
     *
     * @param task the task just unmarked
     */
    public void showUnmarked(Task task) {
        showBlock(" OK, I've marked this task as not done yet:", "   " + task);
    }

    /**
     * Shows the tasks falling on a particular date.
     *
     * @param dateText the date, already formatted for display
     * @param matches  the tasks that fall on that date
     */
    public void showTasksOn(String dateText, List<Task> matches) {
        showLine();
        System.out.println(" Here is what you have on " + dateText + ":");
        if (matches.isEmpty()) {
            System.out.println(" Nothing scheduled. Enjoy the day!");
        }
        for (int i = 0; i < matches.size(); i++) {
            System.out.println(" " + (i + 1) + "." + matches.get(i));
        }
        showLine();
    }

    /**
     * Warns that the save file could not be read in full.
     *
     * @param skipped how many lines had to be discarded
     */
    public void showLoadingError(int skipped) {
        showBlock(" Heads up: I skipped " + skipped + " unreadable line(s) in the save file.",
                " The rest of your tasks loaded fine.");
    }

    /** Prints the given lines framed by divider lines. */
    private void showBlock(String... messages) {
        showLine();
        for (String message : messages) {
            System.out.println(message);
        }
        showLine();
    }
}
