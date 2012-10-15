package utilities;

import storage.TaskRecords;


public class CommandEdit implements Command{
	private static final String MESSAGE_ERROR_CANNOT_EDIT = "Task could not be edited.";
	private static final String MESSAGE_SUCCESS = "Replaced with %s";	
	private static final String MESSAGE_ERROR_CANNOT_FIND_TASK = "Could not find the task specified.";
	private boolean isReversible = true;
	private Task newTask;
	private Task taskToBeReplaced;
	private int indexOfTaskToBeReplaced;
	
	public CommandEdit(int indexOfTaskToBeReplaced, Task newTask){
		this.indexOfTaskToBeReplaced = indexOfTaskToBeReplaced;
		this.newTask = newTask;
	}
	
	public CommandEdit(Task taskToBeReplaced, Task newTask){
		this.taskToBeReplaced = taskToBeReplaced;
		this.newTask = newTask;
	}
	
	public String processCommand(TaskRecords taskRecords) {
		if(taskToBeReplaced == null){
			taskToBeReplaced = taskRecords.getTaskByIndex(indexOfTaskToBeReplaced);
			if(taskToBeReplaced == null){
				return MESSAGE_ERROR_CANNOT_FIND_TASK;
			}
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
		isReversible = isSuccessful;
		if(isSuccessful){
			return String.format(MESSAGE_SUCCESS, newTask.toString());
		}
		return MESSAGE_ERROR_CANNOT_EDIT;
	}
	
	public Command reverseCommand() {
		return new CommandEdit(newTask, taskToBeReplaced);
	}

	public boolean isReversible(){
		return isReversible;
	}
	public CommandType getCommandType(){
		return CommandType.EDIT;
	}
}
