import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Nova {
    /** Save file location, relative to the project root. */
    private static final String DATA_FILE = "data/nova.txt";

    private static final Ui ui = new Ui();
    private static final Storage storage = new Storage(DATA_FILE);

    public static void main(String[] args) {
        ui.showWelcome();
        ArrayList<Task> tasks = loadTasks();

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
                        saveTasks(tasks);
                        ui.showMarked(tasks.get(index));
                        break;
                    }

                    case UNMARK: {
                        int index = parseIndex(input, command, tasks.size());
                        tasks.get(index).markAsNotDone();
                        saveTasks(tasks);
                        ui.showUnmarked(tasks.get(index));
                        break;
                    }

                    case DELETE: {
                        int index = parseIndex(input, command, tasks.size());
                        Task removed = tasks.remove(index);
                        saveTasks(tasks);
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
        saveTasks(tasks);
        ui.showAdded(task, tasks.size());
    }

    /** Persists the task list, telling the user if the write failed. */
    private static void saveTasks(List<Task> tasks) {
        try {
            storage.save(tasks);
        } catch (NovaException e) {
            ui.showError(e.getMessage());
        }
    }

    /**
     * Loads the saved tasks, falling back to an empty list if the file could
     * not be read at all, and warning about any lines that were skipped.
     */
    private static ArrayList<Task> loadTasks() {
        try {
            ArrayList<Task> tasks = new ArrayList<>(storage.load());
            if (storage.getSkippedLineCount() > 0) {
                ui.showLoadingError(storage.getSkippedLineCount());
            }
            return tasks;
        } catch (NovaException e) {
            ui.showError(e.getMessage());
            return new ArrayList<>();
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
