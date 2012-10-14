package utilities;

import storage.TaskRecords;

public interface Command {
	public boolean processCommand(TaskRecords taskRecords);
	public Command reverseCommand();
	public boolean isReversible();
	public CommandType getCommandType();
}