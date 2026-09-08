package nova;

import java.util.Scanner;

/**
 * The console front end: reads commands from standard input and prints the
 * chatbot's replies framed by divider lines.
 *
 * <p>Everything specific to a terminal lives here — the scanner, the divider,
 * and the read-and-reply loop. Keeping it out of {@link Nova} means the
 * chatbot itself has no console concerns and backs the GUI just as readily.
 */
public class Cli {
    private static final String LINE = "____________________________________________________________";

    private final Nova nova;
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Creates a console front end for the given chatbot.
     *
     * @param nova the chatbot to run
     */
    public Cli(Nova nova) {
        this.nova = nova;
    }

    /** Greets the user, then reads and answers commands until told to stop. */
    public void run() {
        printBlock(nova.getGreeting());
        printBlock(nova.loadTasks());

        while (!nova.isExit()) {
            printBlock(nova.getResponse(scanner.nextLine().trim()));
        }

        scanner.close();
    }

    /** Prints a reply framed by divider lines, skipping it when there is nothing to say. */
    private static void printBlock(String message) {
        if (message.isEmpty()) {
            return;
        }
        System.out.println(LINE);
        System.out.println(message);
        System.out.println(LINE);
    }
}
