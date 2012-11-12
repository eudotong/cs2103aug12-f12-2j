package utilities;

import storage.TaskRecords;

/**
 * Interface for Command objects.
 * 
 * @author A0088278L
 */
public interface Command {
	/**
	 * Processes the command and returns appropriate success/failure message.
	 * 
	 * @param taskRecords
	 * @return String
	 */
	public String processCommand(TaskRecords taskRecords);

	/**
	 * Reverses the command.
	 * 
	 * @return Command
	 */
	public Command reverseCommand();

	/**
	 * Returns true if command is reversible. False otherwise.
	 * 
	 * @return boolean
	 */
	public boolean isReversible();

	/**
	 * Returns CommandType of command.
	 * 
	 * @return CommandType
	 */
	public CommandType getCommandType();
}