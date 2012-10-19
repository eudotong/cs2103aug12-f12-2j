package utilities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Task implements Comparable<Task> {
	private static final int POSITIVE_NUMBER = 1;
	private static final int SAME_TIME = 0;
	private static final int NEGATIVE_NUMBER = -1;
	private static final String EMPTY_STRING = "";
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat
			.forPattern("d/M/yyyy hh:mma");
	private static final String TASK_FORMAT = "%s|%s|%s";
	
	private String taskName;
	private DateTime startTime;
	private DateTime endTime;

	public Task(DateTime startTime) {
		this.startTime = startTime;
		taskName = EMPTY_STRING;
	}

	public Task(String taskName, DateTime startTime, DateTime endTime) {
		if (endTime != null && startTime.isAfter(endTime)) {
			this.startTime = endTime;
			this.endTime = startTime;
		} else {
			this.startTime = startTime;
			this.endTime = endTime;
		}
		this.taskName = taskName;
	}

	public Task(String taskName, DateTime startTime) {
		this.taskName = taskName;
		this.startTime = startTime;
		this.endTime = null;
	}

	public String toString() {
		if (endTime == null) {
			return String.format(TASK_FORMAT, startTime.toString(DATE_FORMAT),
					"", taskName);
		}
		return String.format(TASK_FORMAT, startTime.toString(DATE_FORMAT),
				endTime.toString(DATE_FORMAT), taskName);
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
		int compareResult = compareDate(this.startTime, otherTask.getStartTime());
		if (compareResult != SAME_TIME) {
			return compareResult;
		}
		return taskName.compareTo(otherTask.getTaskName());
	}
	
	/**
	 * returns a positive number if firstDate > secondDate
	 * returns SAME_TIME (the number 0) if firstDate is same as secondDate
	 * returns a negative number if firstDate < secondDate
	 * Note: A date that is null is smaller than a date that is not.
	 * 
	 * @param firstDate
	 * @param secondDate
	 * @return int
	 */
	private int compareDate(DateTime firstDate, DateTime secondDate){
		if(firstDate == null && secondDate == null){
			return SAME_TIME;
		}
		if(firstDate == null && secondDate != null){
			return NEGATIVE_NUMBER;
		}
		if(firstDate != null && secondDate == null){
			return POSITIVE_NUMBER;
		}
		return firstDate.compareTo(secondDate);
	}
}
