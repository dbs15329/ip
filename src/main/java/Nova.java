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
            String input = sc.nextLine();

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

            } else if (input.startsWith("mark ")) {
                int index = Integer.parseInt(input.substring(5)) - 1;
                tasks[index].markAsDone();
                System.out.println(LINE);
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + tasks[index]);
                System.out.println(LINE);

            } else if (input.startsWith("unmark ")) {
                int index = Integer.parseInt(input.substring(7)) - 1;
                tasks[index].markAsNotDone();
                System.out.println(LINE);
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + tasks[index]);
                System.out.println(LINE);

            } else if (input.startsWith("todo ")) {
                tasks[count] = new Todo(input.substring(5));
                count++;
                printAdded(tasks[count - 1], count);

            } else if (input.startsWith("deadline ")) {
                String[] parts = input.substring(9).split(" /by ");
                tasks[count] = new Deadline(parts[0], parts[1]);
                count++;
                printAdded(tasks[count - 1], count);

            } else if (input.startsWith("event ")) {
                String[] parts = input.substring(6).split(" /from | /to ");
                tasks[count] = new Event(parts[0], parts[1], parts[2]);
                count++;
                printAdded(tasks[count - 1], count);

            } else {
                System.out.println(LINE);
                System.out.println(" I don't recognise that command.");
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
}