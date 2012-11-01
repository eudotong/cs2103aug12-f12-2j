package utilities;

import storage.TaskRecords;

public class CommandAddAll implements Command{
	private static final boolean IS_REVERSIBLE = true;
	private static final String MESSAGE_SUCCESS = "Tasks added.";
	private Task [] tasksToAdd = new Task[0];
	
	public CommandAddAll(Task [] tasksToBeAdded){
		this.tasksToAdd = tasksToBeAdded;
	}
	@Override
	public String processCommand(TaskRecords taskRecords) {
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
