package utilities;

import org.joda.time.DateTime;

import storage.TaskRecords;


public class CommandSearch implements Command{
	private static final boolean isReversible = false;
	private DateTime startTime;
	private DateTime endTime;
	private String query;
	
	public CommandSearch(DateTime startTime, DateTime endTime, String query){
		this.startTime = startTime;
		this.endTime = endTime;
		this.query = query;
	}
	
	public boolean processCommand(TaskRecords taskRecords) {
		// TODO Auto-generated method stub
		return true;
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
		return isReversible;
	}
}
