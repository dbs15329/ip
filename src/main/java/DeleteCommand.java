/** Removes a task from the list. */
public class DeleteCommand extends Command {
    private final int index;

    /**
     * Creates a command that will delete one task.
     *
     * @param index zero-based position of the task
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        Parser.checkIndexInRange(index, tasks.size());
        Task removed = tasks.remove(index);
        storage.save(tasks.asList());
        ui.showRemoved(removed, tasks.size());
    }
}
