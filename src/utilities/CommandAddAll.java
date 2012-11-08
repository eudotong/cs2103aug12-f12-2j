package utilities;

import storage.TaskRecords;

/**
 * 
 * @author A0088278L
 * 
 *         Functionality for command of type ADD_ALL. This command is not a user
 *         issued command it exists so the command MARK_ALL can be reversed
 */
public class CommandAddAll implements Command {
	private static final boolean IS_REVERSIBLE = true;
	private static final String MESSAGE_SUCCESS = "Tasks added.";
	private Task[] tasksToAdd = new Task[0];

	public CommandAddAll(Task[] tasksToBeAdded) {
		assert tasksToBeAdded != null : "Null array";
		// TODO ask Hieu if this assertion can... but like very waste time
		for (Task task : tasksToBeAdded) {
			assert task != null : "Task in array is null.";
		}
		this.tasksToAdd = tasksToBeAdded;
	}

	@Override
	public String processCommand(TaskRecords taskRecords) {
		assert taskRecords != null : "Null task records.";
		taskRecords.addAll(tasksToAdd);
		return MESSAGE_SUCCESS;
	}

	@Override
	public Command reverseCommand() {
		return new CommandMarkAll(tasksToAdd);
	}

	@Override
	public boolean isReversible() {
		return IS_REVERSIBLE;
	}

	@Override
	public CommandType getCommandType() {
		return null;
	}
}
