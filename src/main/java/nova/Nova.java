package nova;

import nova.command.Command;

/**
 * A command-line chatbot that keeps track of todos, deadlines and events.
 *
 * <p>This class only wires the pieces together and runs the main loop. The
 * work itself is split across {@link Ui} (talking to the user),
 * {@link Storage} (the save file), {@link TaskList} (the tasks themselves),
 * {@link Parser} (reading input) and the {@link Command} classes.
 */
public class Nova {
    /** Save file location, relative to the project root. */
    private static final String DATA_FILE = "data/nova.txt";

    private final Ui ui;
    private final Storage storage;
    private TaskList tasks;

    /**
     * Creates a chatbot that persists its tasks to the given file.
     *
     * @param filePath path to the save file, relative to the project root
     */
    public Nova(String filePath) {
        this.ui = new Ui();
        this.storage = new Storage(filePath);
        this.tasks = new TaskList();
    }

    /** Greets the user, then reads and executes commands until told to stop. */
    public void run() {
        ui.showWelcome();
        tasks = loadTasks();

        boolean isExit = false;
        while (!isExit) {
            try {
                Command command = Parser.parse(ui.readCommand());
                command.execute(tasks, ui, storage);
                isExit = command.isExit();
            } catch (NovaException e) {
                ui.showError(e.getMessage());
            }
        }

        ui.close();
    }

    /**
     * Loads the saved tasks, falling back to an empty list if the file could
     * not be read at all, and warning about any lines that were skipped.
     *
     * <p>Loading happens after the welcome message rather than in the
     * constructor so that any warning appears below the greeting.
     */
    private TaskList loadTasks() {
        try {
            TaskList loaded = new TaskList(storage.load());
            if (storage.getSkippedLineCount() > 0) {
                ui.showLoadingError(storage.getSkippedLineCount());
            }
            return loaded;
        } catch (NovaException e) {
            ui.showError(e.getMessage());
            return new TaskList();
        }
    }

    public static void main(String[] args) {
        new Nova(DATA_FILE).run();
    }
}
