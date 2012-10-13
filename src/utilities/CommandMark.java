package utilities;

import storage.TaskRecords;


public class CommandMark implements Command{
	private static final boolean isReversible = true;
	private Task taskToBeDeleted;
	
	public CommandMark(Task taskToBeDeleted){
		this.taskToBeDeleted = taskToBeDeleted;
	}
	
	@Override
	public boolean processCommand(TaskRecords taskRecords) {
		return true;
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
}
