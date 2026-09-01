package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import nova.command.AddCommand;
import nova.command.Command;
import nova.command.DeleteCommand;
import nova.command.ExitCommand;
import nova.command.FindCommand;
import nova.command.ListCommand;
import nova.command.MarkCommand;
import nova.command.OnCommand;

public class ParserTest {

    @Test
    public void parse_bye_returnsExitCommandThatStopsTheLoop() throws NovaException {
        Command command = Parser.parse("bye");

        assertInstanceOf(ExitCommand.class, command);
        assertTrue(command.isExit());
    }

    @Test
    public void parse_list_returnsListCommandThatDoesNotStopTheLoop() throws NovaException {
        Command command = Parser.parse("list");

        assertInstanceOf(ListCommand.class, command);
        assertFalse(command.isExit());
    }

    @Test
    public void parse_todoWithDescription_returnsAddCommand() throws NovaException {
        assertInstanceOf(AddCommand.class, Parser.parse("todo read book"));
    }

    @Test
    public void parse_todoWithoutDescription_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("todo"));
        assertEquals("The description of a todo cannot be empty.", e.getMessage());

        assertThrows(NovaException.class, () -> Parser.parse("todo    "));
    }

    @Test
    public void parse_wellFormedDeadline_returnsAddCommand() throws NovaException {
        assertInstanceOf(AddCommand.class, Parser.parse("deadline return book /by 2019-12-02 1800"));
    }

    @Test
    public void parse_deadlineWithoutBy_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("deadline return book"));
        assertEquals("A deadline needs a description and a /by time.", e.getMessage());
    }

    @Test
    public void parse_deadlineWithoutDescription_exceptionThrown() {
        assertThrows(NovaException.class, () -> Parser.parse("deadline  /by 2019-12-02"));
    }

    @Test
    public void parse_deadlineWithUnreadableDate_exceptionThrown() {
        assertThrows(NovaException.class, () -> Parser.parse("deadline return book /by someday"));
    }

    @Test
    public void parse_wellFormedEvent_returnsAddCommand() throws NovaException {
        assertInstanceOf(AddCommand.class,
                Parser.parse("event meeting /from 2019-08-06 1400 /to 2019-08-06 1600"));
    }

    @Test
    public void parse_eventMissingTo_exceptionThrown() {
        NovaException e = assertThrows(
                NovaException.class, () -> Parser.parse("event meeting /from 2019-08-06 1400"));
        assertEquals("An event needs a description, a /from time and a /to time.", e.getMessage());
    }

    @Test
    public void parse_markAndUnmarkAndDelete_returnTheMatchingCommands() throws NovaException {
        assertInstanceOf(MarkCommand.class, Parser.parse("mark 1"));
        assertInstanceOf(MarkCommand.class, Parser.parse("unmark 1"));
        assertInstanceOf(DeleteCommand.class, Parser.parse("delete 1"));
    }

    @Test
    public void parse_taskNumberMissing_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("mark"));
        assertEquals("Please tell me which task number to mark.", e.getMessage());

        NovaException f = assertThrows(NovaException.class, () -> Parser.parse("delete"));
        assertEquals("Please tell me which task number to delete.", f.getMessage());
    }

    @Test
    public void parse_taskNumberNotANumber_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("mark abc"));
        assertEquals("'abc' is not a valid task number.", e.getMessage());
    }

    @Test
    public void parse_onWithDate_returnsOnCommand() throws NovaException {
        assertInstanceOf(OnCommand.class, Parser.parse("on 2019-12-02"));
    }

    @Test
    public void parse_onWithoutDate_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("on"));
        assertEquals("Tell me which date to look up, e.g. on 2019-12-02.", e.getMessage());
    }

    @Test
    public void parse_findWithKeyword_returnsFindCommand() throws NovaException {
        assertInstanceOf(FindCommand.class, Parser.parse("find book"));
    }

    @Test
    public void parse_findWithoutKeyword_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("find"));
        assertEquals("Tell me what to search for, e.g. find book.", e.getMessage());
    }

    @Test
    public void parse_unknownKeyword_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.parse("blah"));
        assertEquals("Sorry, I don't know what that means.", e.getMessage());
    }

    @Test
    public void checkIndexInRange_indexInsideList_noExceptionThrown() throws NovaException {
        Parser.checkIndexInRange(0, 3);
        Parser.checkIndexInRange(2, 3);
    }

    @Test
    public void checkIndexInRange_indexPastEndOfList_exceptionThrown() {
        NovaException e = assertThrows(NovaException.class, () -> Parser.checkIndexInRange(3, 3));
        assertEquals("There is no task number 4 in your list.", e.getMessage());
    }

    @Test
    public void checkIndexInRange_negativeIndexOrEmptyList_exceptionThrown() {
        assertThrows(NovaException.class, () -> Parser.checkIndexInRange(-1, 3));
        assertThrows(NovaException.class, () -> Parser.checkIndexInRange(0, 0));
    }
}
