public enum Command {
    BYE("bye"),
    LIST("list"),
    MARK("mark"),
    UNMARK("unmark"),
    DELETE("delete"),
    TODO("todo"),
    DEADLINE("deadline"),
    EVENT("event");

    private final String keyword;

    Command(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public static Command fromInput(String input) throws NovaException {
        for (Command c : Command.values()) {
            if (input.equals(c.keyword) || input.startsWith(c.keyword + " ")) {
                return c;
            }
        }
        throw new NovaException("Sorry, I don't know what that means.");
    }
}
