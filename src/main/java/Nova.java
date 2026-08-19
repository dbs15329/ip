import java.util.Scanner;

public class Nova {
    private static final String LINE = "____________________________________________________________";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Task[] tasks = new Task[100];
        int count = 0;

        System.out.println(LINE);
        System.out.println(" Hello! I'm Nova");
        System.out.println(" What can I do for you?");
        System.out.println(LINE);

        while (true) {
            String input = sc.nextLine().trim();

            try {
                if (input.equals("bye")) {
                    System.out.println(LINE);
                    System.out.println(" Bye. Hope to see you again soon!");
                    System.out.println(LINE);
                    break;

                } else if (input.equals("list")) {
                    System.out.println(LINE);
                    System.out.println(" Here are the tasks in your list:");
                    for (int i = 0; i < count; i++) {
                        System.out.println(" " + (i + 1) + "." + tasks[i]);
                    }
                    System.out.println(LINE);

                } else if (input.startsWith("mark")) {
                    int index = parseIndex(input, "mark", count);
                    tasks[index].markAsDone();
                    System.out.println(LINE);
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks[index]);
                    System.out.println(LINE);

                } else if (input.startsWith("unmark")) {
                    int index = parseIndex(input, "unmark", count);
                    tasks[index].markAsNotDone();
                    System.out.println(LINE);
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks[index]);
                    System.out.println(LINE);

                } else if (input.startsWith("todo")) {
                    String desc = input.substring(4).trim();
                    if (desc.isEmpty()) {
                        throw new NovaException("The description of a todo cannot be empty.");
                    }
                    tasks[count] = new Todo(desc);
                    count++;
                    printAdded(tasks[count - 1], count);

                } else if (input.startsWith("deadline")) {
                    String[] parts = input.substring(8).trim().split(" /by ");
                    if (parts.length < 2 || parts[0].isEmpty()) {
                        throw new NovaException("A deadline needs a description and a /by time.");
                    }
                    tasks[count] = new Deadline(parts[0], parts[1]);
                    count++;
                    printAdded(tasks[count - 1], count);

                } else if (input.startsWith("event")) {
                    String[] parts = input.substring(5).trim().split(" /from | /to ");
                    if (parts.length < 3 || parts[0].isEmpty()) {
                        throw new NovaException("An event needs a description, a /from time and a /to time.");
                    }
                    tasks[count] = new Event(parts[0], parts[1], parts[2]);
                    count++;
                    printAdded(tasks[count - 1], count);

                } else {
                    throw new NovaException("Sorry, I don't know what that means.");
                }

            } catch (NovaException e) {
                System.out.println(LINE);
                System.out.println(" OOPS!!! " + e.getMessage());
                System.out.println(LINE);
            }
        }

        sc.close();
    }

    private static void printAdded(Task task, int count) {
        System.out.println(LINE);
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + count + " tasks in the list.");
        System.out.println(LINE);
    }

    private static int parseIndex(String input, String command, int count) throws NovaException {
        String arg = input.substring(command.length()).trim();
        if (arg.isEmpty()) {
            throw new NovaException("Please tell me which task number to " + command + ".");
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