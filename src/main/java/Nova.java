import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Nova {
    private static final String LINE = "____________________________________________________________";

    /**
     * Location of the save file, relative to the project root. Built with
     * Path.of so that the separator is correct on every operating system.
     */
    private static final Path DATA_FILE = Path.of("data", "nova.txt");

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

        System.out.println(LINE);
        System.out.println(" Hello! I'm Nova");
        System.out.println(" What can I do for you?");
        System.out.println(LINE);

        while (true) {
            String input = sc.nextLine().trim();

            try {
                Command command = Command.fromInput(input);

                switch (command) {
                    case BYE:
                        System.out.println(LINE);
                        System.out.println(" Bye. Hope to see you again soon!");
                        System.out.println(LINE);
                        sc.close();
                        return;

                    case LIST:
                        System.out.println(LINE);
                        System.out.println(" Here are the tasks in your list:");
                        for (int i = 0; i < tasks.size(); i++) {
                            System.out.println(" " + (i + 1) + "." + tasks.get(i));
                        }
                        System.out.println(LINE);
                        break;

                    case MARK: {
                        int index = parseIndex(input, command, tasks.size());
                        tasks.get(index).markAsDone();
                        save(tasks);
                        System.out.println(LINE);
                        System.out.println(" Nice! I've marked this task as done:");
                        System.out.println("   " + tasks.get(index));
                        System.out.println(LINE);
                        break;
                    }

                    case UNMARK: {
                        int index = parseIndex(input, command, tasks.size());
                        tasks.get(index).markAsNotDone();
                        save(tasks);
                        System.out.println(LINE);
                        System.out.println(" OK, I've marked this task as not done yet:");
                        System.out.println("   " + tasks.get(index));
                        System.out.println(LINE);
                        break;
                    }

                    case DELETE: {
                        int index = parseIndex(input, command, tasks.size());
                        Task removed = tasks.remove(index);
                        save(tasks);
                        System.out.println(LINE);
                        System.out.println(" Noted. I've removed this task:");
                        System.out.println("   " + removed);
                        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                        System.out.println(LINE);
                        break;
                    }

                    case TODO: {
                        String desc = input.substring(4).trim();
                        if (desc.isEmpty()) {
                            throw new NovaException("The description of a todo cannot be empty.");
                        }
                        tasks.add(new Todo(desc));
                        save(tasks);
                        printAdded(tasks);
                        break;
                    }

                    case DEADLINE: {
                        String[] parts = input.substring(8).trim().split(" /by ");
                        if (parts.length < 2 || parts[0].isEmpty()) {
                            throw new NovaException("A deadline needs a description and a /by time.");
                        }
                        tasks.add(new Deadline(parts[0], parts[1]));
                        save(tasks);
                        printAdded(tasks);
                        break;
                    }

                    case EVENT: {
                        String[] parts = input.substring(5).trim().split(" /from | /to ");
                        if (parts.length < 3 || parts[0].isEmpty()) {
                            throw new NovaException("An event needs a description, a /from time and a /to time.");
                        }
                        tasks.add(new Event(parts[0], parts[1], parts[2]));
                        save(tasks);
                        printAdded(tasks);
                        break;
                    }

                    default:
                        throw new NovaException("Sorry, I don't know what that means.");
                }

            } catch (NovaException e) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! " + e.getMessage());
                System.out.println(LINE);
            }
        }
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
            System.out.println(LINE);
            System.out.println(" OOPS!!! I couldn't save your tasks: " + e.getMessage());
            System.out.println(LINE);
        }
    }

    private static void printAdded(ArrayList<Task> tasks) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + tasks.get(tasks.size() - 1));
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        System.out.println(LINE);
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
