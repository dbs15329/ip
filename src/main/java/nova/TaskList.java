package nova;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import nova.task.Task;

/**
 * The list of tasks the user is keeping track of.
 *
 * <p>This wraps a plain {@code ArrayList} so that operations on the list —
 * adding, removing, and searching by date — live next to the data instead of
 * being spread through the command handling.
 */
public class TaskList {
    private final List<Task> tasks;

    /** Creates an empty task list. */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Creates a task list holding the given tasks, e.g. the ones just loaded
     * from the save file.
     *
     * @param tasks the initial tasks; copied, so the caller's list is not aliased
     */
    public TaskList(List<Task> tasks) {
        this.tasks = new ArrayList<>(tasks);
    }

    /**
     * Adds a task to the end of the list.
     *
     * @param task the task to add
     */
    public void add(Task task) {
        tasks.add(task);
    }

    /**
     * Removes the task at the given position.
     *
     * @param index zero-based position of the task
     * @return the task that was removed
     */
    public Task remove(int index) {
        return tasks.remove(index);
    }

    /**
     * Returns the task at the given position.
     *
     * @param index zero-based position of the task
     * @return the task at that position
     */
    public Task get(int index) {
        return tasks.get(index);
    }

    /**
     * Returns the number of tasks in the list.
     *
     * @return the task count
     */
    public int size() {
        return tasks.size();
    }

    /**
     * Returns the tasks falling on the given date.
     *
     * @param date the date to search for
     * @return the matching tasks, in list order
     */
    public List<Task> getTasksOn(LocalDate date) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.isOn(date)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /**
     * Returns the tasks whose description contains the given keyword,
     * ignoring case.
     *
     * @param keyword the text to search for
     * @return the matching tasks, in list order
     */
    public List<Task> find(String keyword) {
        List<Task> matches = new ArrayList<>();
        for (Task task : tasks) {
            if (task.hasKeyword(keyword)) {
                matches.add(task);
            }
        }
        return matches;
    }

    /**
     * Returns the tasks as an unmodifiable list, for display and for saving.
     *
     * @return a read-only view of the tasks
     */
    public List<Task> asList() {
        return Collections.unmodifiableList(tasks);
    }
}
