package seedu.address.logic.commands;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;

/**
 * A dummy start command for the start state
 */
public class StartCommand extends Command {

    public static final String COMMAND_WORD = "start";
    public static final String MESSAGE_SHOULD_NOT_EXECUTE = "Start Command should not be executed!";
    private static StartCommand startCommand = null;

    private StartCommand() {
        super.setReversible(true);
    }

    /**
     * @param model {@code Model} which the command should operate on.
     * @throws CommandException if this method is executed
     */
    @Override
    public CommandResult execute(Model model) throws CommandException {
        throw new CommandException(MESSAGE_SHOULD_NOT_EXECUTE);
    }
    @Override
    public String getCommandString() {
        return COMMAND_WORD;
    }

    public static StartCommand getStartCommand() {
        if (startCommand == null) {
            startCommand = new StartCommand();
        }
        return startCommand;
    }
}
