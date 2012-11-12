package utilities;

import storage.TaskRecords;

/**
 * Functionality for command of type MARK_ALL
 * 
 * @author A0088278L
 */
public class CommandMarkAll implements Command {
	private static final String EMPTY_STRING = "";
	private static final String MESSAGE_SUCCESS = "Tasks deleted.";
	private static final boolean IS_REVERSIBLE = true;

	private Task[] tasksToDelete;

	public CommandMarkAll() {
		tasksToDelete = null;
	}

	/**
	 * Initializes a newly created CommandMarkAll object with parameters
	 * specified.
	 * 
	 * @param tasksToDelete
	 */
	public CommandMarkAll(Task[] tasksToDelete) {
		assert tasksToDelete != null : "Null array";
		for (Task task : tasksToDelete) {
			assert task != null : "Task in array is null.";
		}
		this.tasksToDelete = tasksToDelete;
	}

	@Override
	public String processCommand(TaskRecords taskRecords) {
		assert taskRecords != null : "Null task records.";
		if (tasksToDelete == null) {
			tasksToDelete = taskRecords.getCurrentListOfTasks();
		}
		taskRecords.removeAll(tasksToDelete);
		return MESSAGE_SUCCESS;
	}

	@Override
	public Command reverseCommand() {
		return new CommandAddAll(tasksToDelete);
	}

	@Override
	public boolean isReversible() {
		return IS_REVERSIBLE;
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.MARK_ALL;
	}

	// for testing
	public String toString() {
		String commandMarkAllAsString = EMPTY_STRING;
		for (Task task : tasksToDelete) {
			commandMarkAllAsString += task;
		}
		return commandMarkAllAsString;
	}
}
