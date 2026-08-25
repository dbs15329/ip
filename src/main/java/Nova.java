import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Nova {
    /**
     * Location of the save file, relative to the project root. Built with
     * Path.of so that the separator is correct on every operating system.
     */
    private static final Path DATA_FILE = Path.of("data", "nova.txt");

    private static final Ui ui = new Ui();

    public static void main(String[] args) {
        ui.showWelcome();
        ArrayList<Task> tasks = load();

        while (true) {
            String input = ui.readCommand();

            try {
                Command command = Command.fromInput(input);

                switch (command) {
                    case BYE:
                        ui.showGoodbye();
                        ui.close();
                        return;

                    case LIST:
                        ui.showTaskList(tasks);
                        break;

                    case MARK: {
                        int index = parseIndex(input, command, tasks.size());
                        tasks.get(index).markAsDone();
                        save(tasks);
                        ui.showMarked(tasks.get(index));
                        break;
                    }

                    case UNMARK: {
                        int index = parseIndex(input, command, tasks.size());
                        tasks.get(index).markAsNotDone();
                        save(tasks);
                        ui.showUnmarked(tasks.get(index));
                        break;
                    }

                    case DELETE: {
                        int index = parseIndex(input, command, tasks.size());
                        Task removed = tasks.remove(index);
                        save(tasks);
                        ui.showRemoved(removed, tasks.size());
                        break;
                    }

                    case TODO: {
                        String desc = input.substring(4).trim();
                        if (desc.isEmpty()) {
                            throw new NovaException("The description of a todo cannot be empty.");
                        }
                        addTask(tasks, new Todo(desc));
                        break;
                    }

                    case DEADLINE: {
                        String[] parts = input.substring(8).trim().split(" /by ");
                        if (parts.length < 2 || parts[0].isEmpty()) {
                            throw new NovaException("A deadline needs a description and a /by time.");
                        }
                        addTask(tasks, new Deadline(parts[0], DateTimes.parse(parts[1])));
                        break;
                    }

                    case EVENT: {
                        String[] parts = input.substring(5).trim().split(" /from | /to ");
                        if (parts.length < 3 || parts[0].isEmpty()) {
                            throw new NovaException("An event needs a description, a /from time and a /to time.");
                        }
                        addTask(tasks, new Event(parts[0], DateTimes.parse(parts[1]), DateTimes.parse(parts[2])));
                        break;
                    }

                    case ON: {
                        String arg = input.substring(2).trim();
                        if (arg.isEmpty()) {
                            throw new NovaException("Tell me which date to look up, e.g. on 2019-12-02.");
                        }
                        LocalDate date = DateTimes.parseDate(arg);
                        List<Task> matches = new ArrayList<>();
                        for (Task task : tasks) {
                            if (task.isOn(date)) {
                                matches.add(task);
                            }
                        }
                        ui.showTasksOn(DateTimes.formatDate(date), matches);
                        break;
                    }

                    default:
                        throw new NovaException("Sorry, I don't know what that means.");
                }

            } catch (NovaException e) {
                ui.showError(e.getMessage());
            }
        }
    }

    /** Adds a task to the list, saves the list, and confirms to the user. */
    private static void addTask(ArrayList<Task> tasks, Task task) {
        tasks.add(task);
        save(tasks);
        ui.showAdded(task, tasks.size());
    }

    /**
     * Writes the whole task list to the save file, creating the enclosing
     * folder first if it does not exist yet. Rewriting the entire file (rather
     * than appending) keeps the on-disk copy in step with edits and deletions.
     */
    private static void save(List<Task> tasks) {
        List<String> lines = new ArrayList<>();
        for (Task task : tasks) {
            lines.add(task.toFileString());
        }

        try {
            Path parent = DATA_FILE.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.write(DATA_FILE, lines);
        } catch (IOException e) {
            ui.showError("I couldn't save your tasks: " + e.getMessage());
        }
    }

    /**
     * Reads the task list back from the save file.
     *
     * <p>A missing file is not an error: it simply means this is the first
     * run on this machine. Lines that cannot be understood are skipped so
     * that one damaged line does not cost the user the rest of the list.
     *
     * @return the tasks recovered from disk, or an empty list if there are none
     */
    private static ArrayList<Task> load() {
        ArrayList<Task> tasks = new ArrayList<>();
        if (!Files.exists(DATA_FILE)) {
            return tasks;
        }

        List<String> lines;
        try {
            lines = Files.readAllLines(DATA_FILE);
        } catch (IOException e) {
            ui.showError("I couldn't read " + DATA_FILE + ": " + e.getMessage()
                    + " Starting with an empty task list.");
            return tasks;
        }

        int skipped = 0;
        for (String line : lines) {
            if (line.isBlank()) {
                continue;
            }
            try {
                tasks.add(parseSavedTask(line));
            } catch (NovaException e) {
                skipped++;
            }
        }

        if (skipped > 0) {
            ui.showLoadingError(skipped);
        }
        return tasks;
    }

    /**
     * Rebuilds a single task from its save-file line.
     *
     * @param line one line of the save file, e.g. {@code D | 0 | return book | 2019-06-06T00:00}
     * @return the reconstructed task
     * @throws NovaException if the line does not match the expected format
     */
    private static Task parseSavedTask(String line) throws NovaException {
        String[] parts = line.split(" \\| ", -1);
        if (parts.length < 3) {
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
                requireFieldCount(parts, 3);
                task = new Todo(description);
                break;

            case "D":
                requireFieldCount(parts, 4);
                task = new Deadline(description, DateTimes.parse(parts[3]));
                break;

            case "E":
                requireFieldCount(parts, 5);
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
        for (int i = 3; i < parts.length; i++) {
            if (parts[i].isBlank()) {
                throw new NovaException("Field " + (i + 1) + " is empty.");
            }
        }
    }

    private static int parseIndex(String input, Command command, int count) throws NovaException {
        String keyword = command.getKeyword();
        String arg = input.substring(keyword.length()).trim();
        if (arg.isEmpty()) {
            throw new NovaException("Please tell me which task number to " + keyword + ".");
        }
        int index;
        try {
            index = Integer.parseInt(arg) - 1;
        } catch (NumberFormatException e) {
            throw new NovaException("'" + arg + "' is not a valid task number.");
        }
        if (index < 0 || index >= count) {
            throw new NovaException("There is no task number " + (index + 1) + " in your list.");
        }
        return index;
    }
}
