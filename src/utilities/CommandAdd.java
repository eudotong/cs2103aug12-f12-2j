package utilities;

import storage.TaskRecords;

public class CommandAdd implements Command{
	private static final String MESSAGE_ERROR_CANNOT_ADD = "Task could not be added.";
	private static final String MESSAGE_SUCCESS = "%s was added";	
	private boolean isReversible;
	private Task taskToBeAdded;
	
	public CommandAdd(Task taskToBeAdded){
		this.taskToBeAdded = taskToBeAdded;
		isReversible = true;
	}
	public Command reverseCommand(){
		return new CommandMark(taskToBeAdded);
	}
	public String processCommand(TaskRecords taskRecords) {
		boolean isSuccessful = taskRecords.appendTask(taskToBeAdded);
		isReversible = isSuccessful;
		if(isSuccessful){
			return String.format(MESSAGE_SUCCESS, taskToBeAdded.toString());
		}
		return MESSAGE_ERROR_CANNOT_ADD;
	}
	public CommandType getCommandType(){
		return CommandType.ADD;
	}
	public boolean isReversible(){
		return isReversible;
	}
}
