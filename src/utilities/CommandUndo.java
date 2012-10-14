package utilities;

import storage.TaskRecords;

public class CommandUndo implements Command{
	private static final boolean IS_REVERSIBLE = false;
	@Override
	public Command reverseCommand() {
		return null;
	}

	@Override
	public boolean isReversible() {
		return IS_REVERSIBLE;
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.UNDO;
	}

	@Override
	public String processCommand(TaskRecords taskRecords) {
		return "";
	}

}
