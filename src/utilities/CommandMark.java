package utilities;

import storage.TaskRecords;


public class CommandMark implements Command{
	private static final String MESSAGE_ERROR_CANNOT_DELETE = "Task could not be added.";
	private static final String MESSAGE_SUCCESS = "%s was deleted";	
	private boolean isReversible = true;
	private Task taskToBeDeleted;
	
	public CommandMark(Task taskToBeDeleted){
		this.taskToBeDeleted = taskToBeDeleted;
	}
	
	public String processCommand(TaskRecords taskRecords) {
		boolean isSuccessful = taskRecords.deleteTask(taskToBeDeleted);
		isReversible = isSuccessful;
		if(isSuccessful){
			return String.format(MESSAGE_SUCCESS, taskToBeDeleted.toString());
		}
		return MESSAGE_ERROR_CANNOT_DELETE;
	}
	public Command reverseCommand() {
		return new CommandAdd(taskToBeDeleted);
	}
	public CommandType getCommandType(){
		return CommandType.MARK;
	}
	public boolean isReversible(){
		return isReversible;
	}
	public Task getTask(){
		return taskToBeDeleted;
	}
}
