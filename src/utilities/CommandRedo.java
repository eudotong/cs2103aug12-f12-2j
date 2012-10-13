package utilities;

import storage.TaskRecords;

public class CommandRedo implements Command{
	private static final boolean IS_REVERSIBLE = false;
	
	@Override
	public boolean processCommand(TaskRecords taskRecords) {
		return true;
	}

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
		return CommandType.REDO;
	}

}
