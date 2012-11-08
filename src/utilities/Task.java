package utilities;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Task implements Comparable<Task> {
	private static final String COLON = ": ";
	private static final String TIME_TO_TIME = "%s to %s";
	private static final String PIPE_STRING = "|";
	private static final int POSITIVE_NUMBER = 1;
	private static final int SAME_TIME = 0;
	private static final int NEGATIVE_NUMBER = -1;
	private static final String EMPTY_STRING = "";
	private static final String NULL_STRING = "null";
	private static final DateTimeFormatter DATE_FORMATTER_DATE_AND_TIME = DateTimeFormat
			.forPattern("d/M/yyyy hh:mma");
	private static final DateTimeFormatter DATE_FORMATTER_TIME = DateTimeFormat
			.forPattern("hh:mma");
	private static final String TASK_FORMAT = "%s|%s|%s";
	private static final String TASK_NICE_FORMAT = "%s\t|%s";

	private static Logger logger = Logger.getLogger("JIMI");

	private String taskName;
	private DateTime startTime;
	private DateTime endTime;

	public static void main(String[] args) {
		Task t = new Task("asd", null, null);
		t = new Task("B Second Task", new DateTime().minusDays(2), null);
	}

	public Task(DateTime startTime) {
		this.startTime = startTime;
		taskName = EMPTY_STRING;
	}

	public Task(String taskName, DateTime startTime, DateTime endTime) {
		assert (compareNullDatesLast(startTime, endTime) <= SAME_TIME) : "Start time is after end time.";
		this.startTime = startTime;
		this.endTime = endTime;
		this.taskName = changeToValidName(taskName);
		logger.log(Level.INFO, "Task created with parameters: " + taskName
				+ ", " + startTime + ", " + endTime);
	}

	public String getTimesAsString() {
		if (startTime == null) {
			return EMPTY_STRING;
		}
		if (endTime == null) {
			return startTime.toString(DATE_FORMATTER_TIME) + COLON;
		}
		return String.format(TIME_TO_TIME,
				startTime.toString(DATE_FORMATTER_TIME),
				endTime.toString(DATE_FORMATTER_TIME))
				+ COLON;
	}

	private String changeToValidName(String taskName) {
		if (taskName == null) {
			return EMPTY_STRING;
		}
		return taskName.replace(PIPE_STRING, EMPTY_STRING);
	}

	public String toString() {
		return String.format(TASK_FORMAT, convertDateToString(startTime),
				convertDateToString(endTime), taskName);
	}

	private String convertDateToString(DateTime dateToProcess) {
		if (dateToProcess == null) {
			return NULL_STRING;
		}
		return dateToProcess.toString(DATE_FORMATTER_DATE_AND_TIME);
	}

	public boolean isMatch(String query) {
		return taskName.toLowerCase().contains(query.toLowerCase());
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public DateTime getStartTime() {
		return startTime;
	}

	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	public DateTime getEndTime() {
		return endTime;
	}

	public void setEndTime(DateTime endTime) {
		this.endTime = endTime;
	}

	@Override
	public int compareTo(Task otherTask) {
		if (otherTask == null) {
			return NEGATIVE_NUMBER;
		}
		int compareResult = compareDate(this.startTime,
				otherTask.getStartTime());
		if (compareResult != SAME_TIME) {
			return compareResult;
		}
		return taskName.compareTo(otherTask.getTaskName());
	}

	/**
	 * returns a positive number if firstDate > secondDate. returns SAME_TIME
	 * (the number 0) if firstDate is same as secondDate. returns a negative
	 * number if firstDate < secondDate Note: A date that is null is larger than
	 * a date that is not.
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return int
	 */
	private int compareDate(DateTime firstDate, DateTime secondDate) {
		if (firstDate == null && secondDate == null) {
			return SAME_TIME;
		}
		if (firstDate == null && secondDate != null) {
			return NEGATIVE_NUMBER;
		}
		if (firstDate != null && secondDate == null) {
			return POSITIVE_NUMBER;
		}
		return firstDate.compareTo(secondDate);
	}

	private int compareNullDatesLast(DateTime firstDate, DateTime secondDate) {
		if (firstDate == null && secondDate == null) {
			return SAME_TIME;
		}
		if (firstDate == null && secondDate != null) {
			return POSITIVE_NUMBER;
		}
		if (firstDate != null && secondDate == null) {
			return NEGATIVE_NUMBER;
		}
		return firstDate.compareTo(secondDate);
	}
}
