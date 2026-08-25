package nova.command;

import nova.Storage;
import nova.TaskList;
import nova.Ui;

/** Says goodbye and stops the chatbot. */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
