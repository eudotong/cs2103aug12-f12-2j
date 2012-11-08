package utilities;

import storage.TaskRecords;

/**
 * 
 * @author A0088278L
 * 
 *         Functionality for command of type MARK_ALL
 */
public class CommandMarkAll implements Command {
	private static final String MESSAGE_ERROR_CANNOT_DELETE = "Tasks could not be deleted.";
	private static final String MESSAGE_SUCCESS = "Tasks deleted.";

	private boolean isReversible = true;
	private Task[] tasksToDelete;

	public CommandMarkAll() {
		tasksToDelete = null;
	}

	public CommandMarkAll(Task[] tasksToDelete) {
		assert tasksToDelete != null : "Null array";
		// TODO ask Hieu if this assertion can... but like very waste time
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
		isReversible = taskRecords.removeAll(tasksToDelete);
		if (isReversible) {
			return MESSAGE_SUCCESS;
		}
		return MESSAGE_ERROR_CANNOT_DELETE;
	}

	@Override
	public Command reverseCommand() {
		return new CommandAddAll(tasksToDelete);
	}

	@Override
	public boolean isReversible() {
		return isReversible;
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.MARK_ALL;
	}

}
