package utilities;

import storage.TaskRecords;

public class CommandRedo implements Command {
	private static final boolean IS_REVERSIBLE = false;

	@Override
	public String processCommand(TaskRecords taskRecords) {
		return "";
	}

	//TODO IS THIS OKAY?
	@Override
	public Command reverseCommand() {
		assert false : "Called reverse command on irreversible command.";
		return null;
	}

	@Override
	public boolean isReversible() {
		return IS_REVERSIBLE;
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.REDO;
	}

}
