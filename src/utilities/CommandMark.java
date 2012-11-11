package utilities;

import storage.TaskRecords;

/**
 * Functionality for command of type MARK
 * 
 * @author A0088278L
 */
public class CommandMark implements Command {
	private static final String MESSAGE_ERROR_CANNOT_DELETE = "Task could not be deleted.";
	private static final String MESSAGE_SUCCESS = "Task deleted: %s";
	private static final String MESSAGE_ERROR_CANNOT_FIND_TASK = "Could not find the task specified.";

	private boolean isReversible = true;
	private Task taskToBeDeleted;
	private int indexOfTaskToBeDeleted;

	/**
	 * Initializes a newly created CommandMark object with parameters specified.
	 * 
	 * @param indexOfTaskToBeDeleted
	 */
	public CommandMark(int indexOfTaskToBeDeleted) {
		assert indexOfTaskToBeDeleted >= 1 : "Not a valid task index";
		this.indexOfTaskToBeDeleted = indexOfTaskToBeDeleted;
		taskToBeDeleted = null;
	}

	/**
	 * Initializes a newly created CommandMark object with parameters specified.
	 * 
	 * @param taskToBeDeleted
	 */
	public CommandMark(Task taskToBeDeleted) {
		assert taskToBeDeleted != null : "Null task.";
		this.taskToBeDeleted = taskToBeDeleted;
	}

	public String processCommand(TaskRecords taskRecords) {
		assert taskRecords != null : "Null task records.";
		if (taskToBeDeleted == null) {
			taskToBeDeleted = taskRecords
					.getTaskByIndex(indexOfTaskToBeDeleted);
			if (taskToBeDeleted == null) {
				isReversible = false;
				return MESSAGE_ERROR_CANNOT_FIND_TASK;
			}
		}
		boolean isSuccessful = taskRecords.deleteTask(taskToBeDeleted);
		isReversible = isSuccessful;
		if (isSuccessful) {
			return String.format(MESSAGE_SUCCESS, taskToBeDeleted.toString());
		}
		return MESSAGE_ERROR_CANNOT_DELETE;
	}

	public Command reverseCommand() {
		return new CommandAdd(taskToBeDeleted);
	}

	public CommandType getCommandType() {
		return CommandType.MARK;
	}

	public boolean isReversible() {
		return isReversible;
	}

	public String toString() {
		return Integer.toString(indexOfTaskToBeDeleted);
	}
}
