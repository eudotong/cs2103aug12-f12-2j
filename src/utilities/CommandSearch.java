package utilities;

import org.joda.time.DateTime;

import storage.TaskRecords;

public class CommandSearch implements Command {
	private static final int LENGTH_ZERO = 0;
	private static final boolean IS_REVERSIBLE = false;
	private static final String MESSAGE_SUCCESS = "Searched.";

	private DateTime fromDate;
	private DateTime toDate;
	private String query;

	public CommandSearch(String query, DateTime fromDate, DateTime toDate) {
		if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
			this.fromDate = toDate;
			this.toDate = fromDate;
		}
		//TODO IS THIS OKAY?
		if(fromDate == null){
			assert toDate != null : "Invalid from and to dates.";
		}
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.query = convertQueryToCorrectFormat(query);
	}
	
	private String convertQueryToCorrectFormat(String query){
		if(query != null && query.length() == LENGTH_ZERO){
			return null;
		}
		return query;
	}

	public String processCommand(TaskRecords taskRecords) {
		assert taskRecords != null : "Null task records.";
		if (query == null && fromDate == null && toDate == null) {
			taskRecords.setCurrentListOfTasks();
		} else if (query == null && fromDate != null && toDate == null) {
			taskRecords.setCurrentListOfTasks(fromDate);
		} else if (query != null && fromDate == null && toDate == null) {
			taskRecords.setCurrentListOfTasks(query);
		} else if (query == null && fromDate != null && toDate != null) {
			taskRecords.setCurrentListOfTasks(fromDate, toDate);
		} else if (query == null && fromDate != null && toDate == null) {
			taskRecords.setCurrentListOfTasks(query, fromDate);
		} else if (query == null && fromDate != null && toDate == null) {
			taskRecords.setCurrentListOfTasks(query, fromDate, toDate);
		}
		return MESSAGE_SUCCESS;
	}

	@Override
	public Command reverseCommand() {
		assert false : "Called reverse command on irreversible command.";
		return null;
	}

	@Override
	public CommandType getCommandType() {
		return CommandType.SEARCH;
	}

	public boolean isReversible() {
		return IS_REVERSIBLE;
	}
	
	public String toString(){
		return fromDate + " " + toDate + " " + query;
	}
}
