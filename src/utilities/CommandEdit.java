package utilities;

import storage.TaskRecords;

/**
 * Functionality for command of type EDIT
 * 
 * @author A0088278L
 */
public class CommandEdit implements Command {
	private static final String MESSAGE_ERROR_CANNOT_EDIT = "Task could not be edited.";
	private static final String MESSAGE_SUCCESS = "Task replaced with %s";
	private static final String MESSAGE_ERROR_CANNOT_FIND_TASK = "Could not find the task specified.";

	private boolean isReversible = true;
	private Task newTask;
	private Task taskToBeReplaced;
	private int indexOfTaskToBeReplaced;

	/**
	 * Initializes a newly created CommandEdit object with parameters specified.
	 * 
	 * @param indexOfTaskToBeReplaced
	 * @param newTask
	 */
	public CommandEdit(int indexOfTaskToBeReplaced, Task newTask) {
		assert indexOfTaskToBeReplaced >= 1 : "Not a valid index";
		assert newTask != null : "Null task.";
		this.indexOfTaskToBeReplaced = indexOfTaskToBeReplaced;
		this.newTask = newTask;
	}

	/**
	 * Initializes a newly created CommandEdit object with parameters specified.
	 * 
	 * @param taskToBeReplaced
	 * @param newTask
	 */
	public CommandEdit(Task taskToBeReplaced, Task newTask) {
		assert newTask != null : "Null task.";
		assert taskToBeReplaced != null : "Null task.";
		this.taskToBeReplaced = taskToBeReplaced;
		this.newTask = newTask;
	}

	public String processCommand(TaskRecords taskRecords) {
		assert taskRecords != null : "Null task records.";
		if (taskToBeReplaced == null) {
			taskToBeReplaced = taskRecords
					.getTaskByIndex(indexOfTaskToBeReplaced);
			if (taskToBeReplaced == null) {
				return MESSAGE_ERROR_CANNOT_FIND_TASK;
			}
		}
		if (newTask.getTaskName() == null || newTask.getTaskName().isEmpty()) {
			newTask.setTaskName(taskToBeReplaced.getTaskName());
		}
		if (newTask.getStartTime() == null) {
			newTask.setStartTime(taskToBeReplaced.getStartTime());
		}
		if (newTask.getEndTime() == null) {
			newTask.setEndTime(taskToBeReplaced.getEndTime());
		}
		boolean isSuccessful = taskRecords.replaceTask(taskToBeReplaced,
				newTask);
		isReversible = isSuccessful;
		if (isSuccessful) {
			return String.format(MESSAGE_SUCCESS, newTask.toString());
		}
		return MESSAGE_ERROR_CANNOT_EDIT;
	}

	public Command reverseCommand() {
		return new CommandEdit(newTask, taskToBeReplaced);
	}

	public boolean isReversible() {
		return isReversible;
	}

	public CommandType getCommandType() {
		return CommandType.EDIT;
	}

	public String toString() {
		return indexOfTaskToBeReplaced + newTask.toString();
	}
}
