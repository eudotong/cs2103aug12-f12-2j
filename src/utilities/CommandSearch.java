package utilities;

import org.joda.time.DateTime;

import storage.TaskRecords;


public class CommandSearch implements Command{
	private static final boolean IS_REVERSIBLE = false;
	private DateTime startTime;
	private DateTime endTime;
	private String query;
	
	public CommandSearch(DateTime startTime, DateTime endTime, String query){
		this.startTime = startTime;
		this.endTime = endTime;
		this.query = query;
	}
	
	public String processCommand(TaskRecords taskRecords) {
		return "";
	}
	@Override
	public Command reverseCommand() {
		return null;
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.SEARCH;
	}
	public boolean isReversible(){
		return IS_REVERSIBLE;
	}
}
