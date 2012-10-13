package utilities;

import storage.TaskRecords;

public class CommandAdd implements Command{
	private static final boolean IS_REVERSIBLE = true;
	
	private Task taskToBeAdded;
	
	public CommandAdd(Task taskToBeAdded){
		this.taskToBeAdded = taskToBeAdded;
	}
	public Command reverseCommand(){
		return new CommandMark(taskToBeAdded);
	}
	public boolean processCommand(TaskRecords taskRecords) {
		return taskRecords.appendTask(taskToBeAdded);
	}
	public CommandType getCommandType(){
		return CommandType.ADD;
	}
	public boolean isReversible(){
		return IS_REVERSIBLE;
	}
}
