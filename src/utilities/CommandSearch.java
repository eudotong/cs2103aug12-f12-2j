package utilities;

import org.joda.time.DateTime;

import storage.TaskRecords;


public class CommandSearch implements Command{
	private static final boolean IS_REVERSIBLE = false;
	private DateTime fromDate;
	private DateTime toDate;
	private String query;
	
	public CommandSearch(DateTime fromDate, DateTime toDate, String query){
		if(fromDate != null && toDate != null && fromDate.isAfter(toDate)){
			this.fromDate = toDate;
			this.toDate = fromDate;
		}
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.query = query;
	}
	
	public String processCommand(TaskRecords taskRecords) {
		if(query == null && fromDate != null && toDate == null){
			taskRecords.setCurrentListOfTasks(fromDate);
		}else if (query != null && fromDate == null && toDate == null){
			taskRecords.setCurrentListOfTasks(query);
		}else if (query == null && fromDate != null && toDate != null){
			taskRecords.setCurrentListOfTasks(fromDate, toDate);
		}else if (query == null && fromDate != null && toDate == null){
			taskRecords.setCurrentListOfTasks(query, fromDate);
		}else if (query == null && fromDate != null && toDate == null){
			taskRecords.setCurrentListOfTasks(query, fromDate, toDate);
		}
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
