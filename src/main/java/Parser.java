/**
 * Turns a line of user input into the {@link Command} it describes.
 *
 * <p>This is where all the string handling lives: splitting off the keyword,
 * finding the {@code /by}, {@code /from} and {@code /to} markers, and reading
 * task numbers. Everything downstream works with typed objects instead.
 */
public class Parser {

    /**
     * Parses a full line of user input.
     *
     * @param input the line the user typed
     * @return the command it describes, ready to execute
     * @throws NovaException if the line is not a command the chatbot understands,
     *                       or if the command is missing required parts
     */
    public static Command parse(String input) throws NovaException {
        CommandType type = CommandType.fromInput(input);
        String arguments = type.argumentsIn(input);

        switch (type) {
            case BYE:
                return new ExitCommand();

            case LIST:
                return new ListCommand();

            case MARK:
                return new MarkCommand(parseIndex(arguments, type), true);

            case UNMARK:
                return new MarkCommand(parseIndex(arguments, type), false);

            case DELETE:
                return new DeleteCommand(parseIndex(arguments, type));

            case TODO:
                if (arguments.isEmpty()) {
                    throw new NovaException("The description of a todo cannot be empty.");
                }
                return new AddCommand(new Todo(arguments));

            case DEADLINE:
                return new AddCommand(parseDeadline(arguments));

            case EVENT:
                return new AddCommand(parseEvent(arguments));

            case ON:
                if (arguments.isEmpty()) {
                    throw new NovaException("Tell me which date to look up, e.g. on 2019-12-02.");
                }
                return new OnCommand(DateTimes.parseDate(arguments));

            default:
                throw new NovaException("Sorry, I don't know what that means.");
        }
    }

    /**
     * Checks that a task number refers to a task that actually exists.
     *
     * <p>This cannot be done while parsing, because the parser does not know
     * how long the list is; it is checked when the command runs.
     *
     * @param index zero-based position the user asked for
     * @param count number of tasks currently in the list
     * @throws NovaException if there is no task at that position
     */
    public static void checkIndexInRange(int index, int count) throws NovaException {
        if (index < 0 || index >= count) {
            throw new NovaException("There is no task number " + (index + 1) + " in your list.");
        }
    }

    /** Reads the task number that follows a mark, unmark or delete keyword. */
    private static int parseIndex(String arguments, CommandType type) throws NovaException {
        if (arguments.isEmpty()) {
            throw new NovaException("Please tell me which task number to " + type.getKeyword() + ".");
        }
        try {
            return Integer.parseInt(arguments) - 1;
        } catch (NumberFormatException e) {
            throw new NovaException("'" + arguments + "' is not a valid task number.");
        }
    }

    /** Builds a Deadline from the text after the deadline keyword. */
    private static Deadline parseDeadline(String arguments) throws NovaException {
        String[] parts = arguments.split(" /by ");
        if (parts.length < 2 || parts[0].isEmpty()) {
            throw new NovaException("A deadline needs a description and a /by time.");
        }
        return new Deadline(parts[0], DateTimes.parse(parts[1]));
    }

    /** Builds an Event from the text after the event keyword. */
    private static Event parseEvent(String arguments) throws NovaException {
        String[] parts = arguments.split(" /from | /to ");
        if (parts.length < 3 || parts[0].isEmpty()) {
            throw new NovaException("An event needs a description, a /from time and a /to time.");
        }
        return new Event(parts[0], DateTimes.parse(parts[1]), DateTimes.parse(parts[2]));
    }
}
