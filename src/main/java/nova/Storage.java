package nova;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

/**
 * Loads tasks from the save file and writes them back to it.
 *
 * <p>The file holds one task per line, with fields separated by
 * {@code " | "}, for example {@code D | 0 | return book | 2019-06-06T00:00}.
 * This class knows that format and nothing else about the chatbot; it never
 * prints anything, so the caller decides how problems are reported.
 */
public class Storage {
    /** Number of fields on a todo line: type, done flag, description. */
    private static final int TODO_FIELD_COUNT = 3;

    /** Number of fields on a deadline line: the todo fields plus a due date. */
    private static final int DEADLINE_FIELD_COUNT = 4;

    /** Number of fields on an event line: the todo fields plus a start and an end. */
    private static final int EVENT_FIELD_COUNT = 5;

    /** Index of the first field that is specific to a task type. */
    private static final int FIRST_TYPE_SPECIFIC_FIELD = 3;

    private final Path file;

    /** Number of unreadable lines skipped during the most recent load. */
    private int skippedLineCount = 0;

    /**
     * Creates a storage backed by the given file.
     *
     * @param filePath path to the save file, relative to the project root
     */
    public Storage(String filePath) {
        this.file = Path.of(filePath);
    }

    /**
     * Reads the task list back from the save file.
     *
     * <p>A missing file is not an error: it simply means this is the first
     * run on this machine. Lines that cannot be understood are skipped, and
     * counted in {@link #getSkippedLineCount()}, so that one damaged line
     * does not cost the user the rest of the list.
     *
     * @return the tasks recovered from disk, or an empty list if there are none
     * @throws NovaException if the file exists but cannot be read
     */
    public List<Task> load() throws NovaException {
        skippedLineCount = 0;
        List<Task> tasks = new ArrayList<>();
        if (!Files.exists(file)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(file);
        } catch (IOException e) {
            throw new NovaException("I couldn't read " + file + ": " + e.getMessage()
                    + " Starting with an empty task list.");
        }

        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseSavedTask(line));
            } catch (NovaException e) {
                skippedLineCount++;
            }
        }
        return tasks;
    }

    /**
     * Writes the whole task list to the save file, creating the enclosing
     * folder first if it does not exist yet. Rewriting the entire file rather
     * than appending keeps the on-disk copy in step with edits and deletions.
     *
     * @param tasks the tasks to persist
     * @throws NovaException if the file cannot be written
     */
    public void save(List<Task> tasks) throws NovaException {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toFileString());
        }

        try {
            Path parent = file.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(file, lines);
        } catch (IOException e) {
            throw new NovaException("I couldn't save your tasks: " + e.getMessage());
        }
    }

    /**
     * Returns how many unreadable lines were skipped by the most recent
     * {@link #load()}.
     *
     * @return the number of skipped lines, zero if the file was intact
     */
    public int getSkippedLineCount() {
        return skippedLineCount;
    }

    /**
     * Rebuilds a single task from its save-file line.
     *
     * @param line one line of the save file
     * @return the reconstructed task
     * @throws NovaException if the line does not match the expected format
     */
    private static Task parseSavedTask(String line) throws NovaException {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < TODO_FIELD_COUNT) {
            throw new NovaException("Line has too few fields.");
        }

        String doneFlag = parts[1];
        if (!doneFlag.equals("0") && !doneFlag.equals("1")) {
            throw new NovaException("Done flag must be 0 or 1.");
        }

        String description = parts[2];
        if (description.isBlank()) {
            throw new NovaException("Description is empty.");
        }

        Task task;
        switch (parts[0]) {
            case "T":
                requireFieldCount(parts, TODO_FIELD_COUNT);
                task = new Todo(description);
                break;

            case "D":
                requireFieldCount(parts, DEADLINE_FIELD_COUNT);
                task = new Deadline(description, DateTimes.parse(parts[3]));
                break;

            case "E":
                requireFieldCount(parts, EVENT_FIELD_COUNT);
                task = new Event(description, DateTimes.parse(parts[3]), DateTimes.parse(parts[4]));
                break;

            default:
                throw new NovaException("Unknown task type '" + parts[0] + "'.");
        }

        if (doneFlag.equals("1")) {
            task.markAsDone();
        }
        return task;
    }

    /**
     * Checks that a save-file line has exactly the expected number of fields
     * and that none of the trailing fields is blank.
     */
    private static void requireFieldCount(String[] parts, int expected) throws NovaException {
        if (parts.length != expected) {
            throw new NovaException("Expected " + expected + " fields but found " + parts.length + ".");
        }
        for (int i = FIRST_TYPE_SPECIFIC_FIELD; i < parts.length; i++) {
            if (parts[i].isBlank()) {
                throw new NovaException("Field " + (i + 1) + " is empty.");
            }
        }
    }
}
