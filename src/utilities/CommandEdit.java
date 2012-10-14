package utilities;

import storage.TaskRecords;


public class CommandEdit implements Command{
	private boolean isReversible = true;
	private String originalCommand;
	private Task taskToBeReplaced;
	private Task newTask;
	
	public String processCommand(TaskRecords taskRecords) {
		return "";
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
