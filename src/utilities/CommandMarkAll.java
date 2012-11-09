package utilities;

import storage.TaskRecords;

/**
 * 
 * @author A0088278L
 * 
 *         Functionality for command of type MARK_ALL
 */
public class CommandMarkAll implements Command {
	private static final String MESSAGE_SUCCESS = "Tasks deleted.";
	private static final boolean IS_REVERSIBLE = true;
	
	private Task[] tasksToDelete;

	public CommandMarkAll() {
		tasksToDelete = null;
	}

	/**
	 * Initializes a newly created CommandMarkAll object with parameters specified.
	 * @param tasksToDelete
	 */
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

}
