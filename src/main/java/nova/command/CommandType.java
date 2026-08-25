package nova.command;

import nova.NovaException;

/**
 * The keywords the chatbot understands, and the mapping from a line of user
 * input to the kind of command it starts.
 */
public enum CommandType {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event"),
    ON("on"),
    FIND("find");

    private final String keyword;

    CommandType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Returns the word the user types to invoke this command.
     *
     * @return the keyword, e.g. {@code deadline}
     */
    public String getKeyword() {
        return keyword;
    }

    /**
     * Returns everything the user typed after the keyword.
     *
     * @param input the full line of input, which must start with this keyword
     * @return the arguments, trimmed, or an empty string if there were none
     */
    public String argumentsIn(String input) {
        return input.substring(keyword.length()).trim();
    }

    /**
     * Works out which command a line of input starts with.
     *
     * @param input the full line of input
     * @return the matching command type
     * @throws NovaException if the line does not start with a known keyword
     */
    public static CommandType fromInput(String input) throws NovaException {
        for (CommandType c : CommandType.values()) {
            if (input.equals(c.keyword) || input.startsWith(c.keyword + " ")) {
                return c;
            }
        }
        throw new NovaException("Sorry, I don't know what that means.");
    }
}
