package utilities;

import storage.TaskRecords;


public class CommandEdit implements Command{
	private static final boolean isReversible = true;
	
	@Override
	public boolean processCommand(TaskRecords taskRecords) {
		return true;
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
