package utilities;

import storage.TaskRecords;


public class CommandEdit implements Command{
	private static final String MESSAGE_ERROR_CANNOT_EDIT = "Task could not be edited.";
	private static final String MESSAGE_SUCCESS = "Replaced with %s";	
	private static final String MESSAGE_ERROR_CANNOT_FIND_TASK = "Could not find the task specified.";
	private boolean isReversible = true;
	private Task newTask;
	private int indexOfTaskToBeReplaced;
	
	public CommandEdit(int indexOfTaskToBeReplaced, Task newTask){
		this.indexOfTaskToBeReplaced = indexOfTaskToBeReplaced;
		this.newTask = newTask;
	}
	
	public String processCommand(TaskRecords taskRecords) {
		Task taskToBeReplaced = taskRecords.getTaskByIndex(indexOfTaskToBeReplaced);
		if(taskToBeReplaced == null){
			return MESSAGE_ERROR_CANNOT_FIND_TASK;
		}
		if(newTask.getTaskName() == null){
			newTask.setTaskName(taskToBeReplaced.getTaskName());
		}
		if(newTask.getStartTime() == null){
			newTask.setStartTime(taskToBeReplaced.getStartTime());
		}
		if(newTask.getEndTime() == null){
			newTask.setEndTime(taskToBeReplaced.getEndTime());
		}
		boolean isSuccessful = taskRecords.replaceTask(taskToBeReplaced, newTask);
		if(isSuccessful){
			return String.format(MESSAGE_SUCCESS, newTask.toString());
		}
		return MESSAGE_ERROR_CANNOT_EDIT;
	}
	
	public Command reverseCommand() {
		return null;
	}

	public boolean isReversible(){
		return isReversible;
	}
	public CommandType getCommandType(){
		return CommandType.EDIT;
	}
}
