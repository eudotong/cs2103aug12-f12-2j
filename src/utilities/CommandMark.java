package utilities;

import storage.TaskRecords;


public class CommandMark implements Command{
	private static final boolean IS_REVERSIBLE = true;
	private Task taskToBeDeleted;
	
	public CommandMark(Task taskToBeDeleted){
		this.taskToBeDeleted = taskToBeDeleted;
	}
	
	public boolean processCommand(TaskRecords taskRecords) {
		return taskRecords.deleteTask(taskToBeDeleted);
	}
	public Command reverseCommand() {
		return new CommandAdd(taskToBeDeleted);
	}
	public CommandType getCommandType(){
		return CommandType.MARK;
	}
	public boolean isReversible(){
		return IS_REVERSIBLE;
	}
}
