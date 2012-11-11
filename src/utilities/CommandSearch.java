package utilities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import storage.TaskRecords;

/**
 * Functionality for command of type SEARCH
 * 
 * @author A0088278L
 */
public class CommandSearch implements Command {
	private static final String AFTER = "onwards";
	private static final String BEFORE = "before";
	private static final DateTime IMPOSSIBLY_LARGE_DATE = new DateTime(
			Long.MAX_VALUE);
	private static final DateTime IMPOSSIBLY_SMALL_DATE = new DateTime(
			Long.MIN_VALUE);
	private static final String UPCOMING = "Searched upcoming tasks.";
	private static final String NULL = "null";
	private static final boolean IS_REVERSIBLE = false;
	private static final String MESSAGE_SUCCESS = "Searched: %s|%s|%s";
	private static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern("dd/MM/yyyy HH:mm");

	private DateTime fromDate;
	private DateTime toDate;
	private String query;

	/**
	 * Initializes a newly created CommandSearch object with parameters
	 * specified.
	 * 
	 * @param query
	 * @param fromDate
	 * @param toDate
	 */
	public CommandSearch(String query, DateTime fromDate, DateTime toDate) {
		if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
			this.fromDate = toDate;
			this.toDate = fromDate;
		}
		this.fromDate = fromDate;
		this.toDate = toDate;
		this.query = query;
	}

	public String processCommand(TaskRecords taskRecords) {
		assert taskRecords != null : "Null task records.";
		if (query == null && fromDate == null && toDate == null) {
			taskRecords.setCurrentListOfTasks();
		} else if (fromDate == null && toDate == null) {
			taskRecords.setCurrentListOfTasks(query);
		} else if (fromDate != null && toDate == null) {
			taskRecords.setCurrentListOfTasks(query, fromDate);
		} else if (fromDate != null && toDate != null) {
			taskRecords.setCurrentListOfTasks(query, fromDate, toDate);
		}
		return getSuccessMessage();
	}
	
	private String getSuccessMessage(){
		if(query == null){
			return UPCOMING;
		}
		String fromDateString = NULL;
		String toDateString = NULL;
		if(fromDate != null){
			fromDateString = fromDate.toString(DATE_FORMATTER);
			if(fromDate.equals(IMPOSSIBLY_SMALL_DATE)){
				fromDateString = BEFORE;
			}
		}
		if(toDate != null){
			toDateString = toDate.toString(DATE_FORMATTER);
			if(toDate.equals(IMPOSSIBLY_LARGE_DATE)){
				toDateString = AFTER;
			}
		}
		return String.format(MESSAGE_SUCCESS, query, fromDateString, toDateString);
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

	//for testing
	public String toString() {
		String fromDateString = NULL;
		String toDateString = NULL;
		if (fromDate != null) {
			fromDateString = fromDate.toString(DATE_FORMATTER);
		}
		if (toDate != null) {
			toDateString = toDate.toString(DATE_FORMATTER);
		}
		return query + fromDateString + toDateString;
	}
}
