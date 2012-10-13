package utilities;

import storage.TaskRecords;


public class CommandEdit implements Command{
	private static final boolean IS_REVERSIBLE = true;
	
	public boolean processCommand(TaskRecords taskRecords) {
		return true;
	}
	
	public Command reverseCommand() {
		return null;
	}

	public boolean isReversible(){
		return IS_REVERSIBLE;
	}
	public CommandType getCommandType(){
		return CommandType.EDIT;
	}
}
