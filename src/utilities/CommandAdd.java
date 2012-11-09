package utilities;

import storage.TaskRecords;

/**
 * 
 * @author A0088278L
 * 
 *         Functionality for command of type ADD
 */
public class CommandAdd implements Command {
	private static final String MESSAGE_ERROR_CANNOT_ADD = "Task could not be added.";
	private static final String MESSAGE_SUCCESS = "%s was added";
	

	private boolean isReversible = true;
	private Task taskToBeAdded;

	/**
	 * Initializes a newly created CommandAdd object with parameters specified.
	 * @param taskToBeAdded
	 */
	public CommandAdd(Task taskToBeAdded) {
		assert taskToBeAdded != null : "Null task.";
		assert taskToBeAdded.getTaskName() != null : "Task has null name.";
		this.taskToBeAdded = taskToBeAdded;
	}

	public Command reverseCommand() {
		return new CommandMark(taskToBeAdded);
	}

	public String processCommand(TaskRecords taskRecords) {
		assert taskRecords != null : "Null task records.";
		boolean isSuccessful = taskRecords.appendTask(taskToBeAdded);
		isReversible = isSuccessful;
		if (isSuccessful) {
			return String.format(MESSAGE_SUCCESS, taskToBeAdded.toString());
		}
		return MESSAGE_ERROR_CANNOT_ADD;
	}

	public CommandType getCommandType() {
		return CommandType.ADD;
	}

	public boolean isReversible() {
		return isReversible;
	}

	// for testing
	public String toString() {
		return taskToBeAdded.toString();
	}
}
