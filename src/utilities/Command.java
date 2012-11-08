package utilities;

import storage.TaskRecords;

/**
 * 
 * @author A0088278L
 * 
 *         Interface Command
 */
public interface Command {
	public String processCommand(TaskRecords taskRecords);

	public Command reverseCommand();

	public boolean isReversible();

	public CommandType getCommandType();
}