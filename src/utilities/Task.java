package utilities;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Task class is data structure for a task.
 * 
 * @author A0088278L
 */
public class Task implements Comparable<Task> {
	private static final String COLON = ": ";
	private static final String TIME_TO_TIME = "%s to %s";
	private static final String PIPE_STRING = "|";
	private static final int POSITIVE_NUMBER = 1;
	private static final int SAME_TIME = 0;
	private static final int NEGATIVE_NUMBER = -1;
	private static final String EMPTY_STRING = "";
	private static final String NULL_STRING = "null";
	private static final DateTimeFormatter DATE_FORMATTER_ = DateTimeFormat
			.forPattern("d/M/yyyy hh:mma");
	private static final DateTimeFormatter DATE_FORMATTER_TIME = DateTimeFormat
			.forPattern("hh:mma");
	private static final DateTimeFormatter DATE_FORMATTER_SHORT_DATE_TIME = DateTimeFormat.forPattern("d/M/YY hh:mma");
	private static final String TASK_FORMAT = "%s|%s|%s";

	private static Logger logger = Logger.getLogger("JIMI");

	private String taskName;
	private DateTime startTime;
	private DateTime endTime;

	/**
	 * Initializes a newly created Task object with start time specified. Task
	 * name is set to "". This constructor is used to create dummy tasks to
	 * facilitate searching.
	 * 
	 * @param startTime
	 */
	public Task(DateTime startTime) {
		this.startTime = startTime;
		taskName = EMPTY_STRING;
	}

	/**
	 * Initializes a newly created Task object with parameters specified.
	 * 
	 * @param taskName
	 * @param startTime
	 * @param endTime
	 */
	public Task(String taskName, DateTime startTime, DateTime endTime) {
		assert (compareNullDatesLast(startTime, endTime) <= SAME_TIME) : "Start time is after end time.";
		this.startTime = startTime;
		this.endTime = endTime;
		this.taskName = changeToValidName(taskName);
		logger.log(Level.INFO, "Task created with parameters: " + taskName
				+ ", " + startTime + ", " + endTime);
	}

	/**
	 * Returns a task's time in hh:mma format. If 2 times then format is hh:mma to hh:mma.
	 * 
	 * @return String
	 */
	public String getTimesAsString() {
		if (startTime == null) {
			return EMPTY_STRING;
		}
		if (endTime == null) {
			return startTime.toString(DATE_FORMATTER_TIME) + COLON;
		}
		return String.format(TIME_TO_TIME,
				startTime.toString(DATE_FORMATTER_TIME),
				endTime.toString(DATE_FORMATTER_SHORT_DATE_TIME))
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
		return dateToProcess.toString(DATE_FORMATTER_);
	}

	/**
	 * True if task name matches query. False otherwise.
	 * 
	 * @param query
	 * @return boolean
	 */
	public boolean isMatch(String query) {
		assert (query != null) : "Null String.";
		return taskName.toLowerCase().contains(query.toLowerCase());
	}

	/**
	 * Returns task name.
	 * 
	 * @return String
	 */
	public String getTaskName() {
		return taskName;
	}

	/**
	 * Sets task name to task name specified.
	 * 
	 * @param taskName
	 */
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	/**
	 * Returns start time.
	 * 
	 * @return DateTime
	 */
	public DateTime getStartTime() {
		return startTime;
	}

	/**
	 * Sets start time to start time specified.
	 * 
	 * @param startTime
	 */
	public void setStartTime(DateTime startTime) {
		this.startTime = startTime;
	}

	/**
	 * Returns end time.
	 * 
	 * @return DateTime
	 */
	public DateTime getEndTime() {
		return endTime;
	}

	/**
	 * Sets end time to end time specified.
	 * 
	 * @param endTime
	 */
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
