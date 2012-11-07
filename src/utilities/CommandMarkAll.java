package utilities;

import storage.TaskRecords;

public class CommandMarkAll implements Command {
	private static final String MESSAGE_ERROR_CANNOT_DELETE = "Tasks could not be deleted.";
	private static final String MESSAGE_SUCCESS = "Tasks deleted.";

	private boolean isReversible = true;
	private Task[] tasksToDelete;
	
	public CommandMarkAll(){
		tasksToDelete = null;
	}
	
	public CommandMarkAll(Task [] tasksToDelete){
		this.tasksToDelete = tasksToDelete;
	}

	@Override
	public String processCommand(TaskRecords taskRecords) {
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
