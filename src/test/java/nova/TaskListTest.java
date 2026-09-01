package nova;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import nova.task.Deadline;
import nova.task.Event;
import nova.task.Task;
import nova.task.Todo;

public class TaskListTest {

    private static final LocalDateTime AUG_6_2PM = LocalDateTime.of(2019, 8, 6, 14, 0);
    private static final LocalDateTime AUG_8_4PM = LocalDateTime.of(2019, 8, 8, 16, 0);

    @Test
    public void size_newList_isZero() {
        assertEquals(0, new TaskList().size());
    }

    @Test
    public void add_severalTasks_keptInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"), new Todo("second"));

        assertEquals(2, tasks.size());
        assertEquals("[T][ ] first", tasks.get(0).toString());
        assertEquals("[T][ ] second", tasks.get(1).toString());
    }

    @Test
    public void add_severalTasksAtOnce_sameAsAddingThemOneByOne() {
        TaskList atOnce = new TaskList();
        atOnce.add(new Todo("first"), new Todo("second"));

        TaskList oneByOne = new TaskList();
        oneByOne.add(new Todo("first"));
        oneByOne.add(new Todo("second"));

        assertEquals(oneByOne.size(), atOnce.size());
        assertEquals(oneByOne.get(0).toString(), atOnce.get(0).toString());
        assertEquals(oneByOne.get(1).toString(), atOnce.get(1).toString());
    }

    @Test
    public void add_noTasks_listUnchanged() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));

        tasks.add();

        assertEquals(1, tasks.size());
    }

    @Test
    public void remove_middleTask_returnsItAndClosesTheGap() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"), new Todo("second"), new Todo("third"));

        Task removed = tasks.remove(1);

        assertEquals("[T][ ] second", removed.toString());
        assertEquals(2, tasks.size());
        assertEquals("[T][ ] third", tasks.get(1).toString());
    }

    @Test
    public void constructor_givenList_doesNotAliasIt() {
        List<Task> source = new ArrayList<>();
        source.add(new Todo("first"));
        TaskList tasks = new TaskList(source);

        source.add(new Todo("added later"));

        assertEquals(1, tasks.size());
    }

    @Test
    public void asList_anyList_isReadOnly() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("first"));

        List<Task> view = tasks.asList();

        assertEquals(1, view.size());
        assertThrows(UnsupportedOperationException.class, () -> view.add(new Todo("sneaky")));
    }

    @Test
    public void getTasksOn_mixedTaskTypes_returnsOnlyMatchesInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("no date"),
                new Deadline("due that day", AUG_6_2PM),
                new Deadline("due later", AUG_8_4PM),
                new Event("spans the day", AUG_6_2PM, AUG_8_4PM));

        List<Task> matches = tasks.getTasksOn(LocalDate.of(2019, 8, 6));

        assertEquals(2, matches.size());
        assertEquals("due that day", matchDescription(matches.get(0)));
        assertEquals("spans the day", matchDescription(matches.get(1)));
    }

    @Test
    public void getTasksOn_dayInsideEventSpanOnly_returnsTheEvent() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("due elsewhere", AUG_6_2PM),
                new Event("spans the day", AUG_6_2PM, AUG_8_4PM));

        List<Task> matches = tasks.getTasksOn(LocalDate.of(2019, 8, 7));

        assertEquals(1, matches.size());
        assertEquals("spans the day", matchDescription(matches.get(0)));
    }

    @Test
    public void getTasksOn_noTaskOnThatDate_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Deadline("due that day", AUG_6_2PM));

        assertEquals(0, tasks.getTasksOn(LocalDate.of(2020, 1, 1)).size());
    }

    @Test
    public void find_keywordInSomeDescriptions_returnsOnlyThoseInOrder() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"),
                new Todo("buy milk"),
                new Deadline("return book", AUG_6_2PM));

        List<Task> matches = tasks.find("book");

        assertEquals(2, matches.size());
        assertEquals("read book", matchDescription(matches.get(0)));
        assertEquals("return book", matchDescription(matches.get(1)));
    }

    @Test
    public void find_differentCase_stillMatches() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("Read Book"));

        assertEquals(1, tasks.find("book").size());
        assertEquals(1, tasks.find("BOOK").size());
    }

    @Test
    public void find_partialWord_matches() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("bookkeeping"));

        assertEquals(1, tasks.find("book").size());
    }

    @Test
    public void find_keywordAbsent_returnsEmptyList() {
        TaskList tasks = new TaskList();
        tasks.add(new Todo("read book"));

        assertEquals(0, tasks.find("bicycle").size());
    }

    /** Pulls the description out of a task's save-file line, for readable assertions. */
    private static String matchDescription(Task task) {
        return task.toFileString().split(" \\| ", -1)[2];
    }
}
