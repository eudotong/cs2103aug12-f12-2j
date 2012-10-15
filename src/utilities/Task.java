package utilities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Task implements Comparable<Task>{
	private static final int SAME_START_TIMES = 0;
	private static final int POSITIVE_NUMBER = 1;
	private static final String EMPTY_STRING = "";
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat
			.forPattern("d/M/yyyy hh:mma");
	private static final String TASK_FORMAT = "%s|%s|%s|%s";
	private String taskName;
	private DateTime startTime;
	private DateTime endTime;
	private boolean isImportant;
	
	public Task(DateTime startTime){
		this.startTime = startTime;
		taskName = EMPTY_STRING;
		isImportant = false;
	}
	
	public Task(String taskName, DateTime startTime, DateTime endTime,
			boolean isImportant) {
		if (endTime != null && startTime.isAfter(endTime)) {
			this.startTime = endTime;
			this.endTime = startTime;
		} else {
			this.startTime = startTime;
			this.endTime = endTime;
		}
		this.taskName = taskName;
		this.isImportant = isImportant;
	}
	
	public Task(String taskName, DateTime startTime, boolean isImportant) {
		this.taskName = taskName;
		this.startTime = startTime;
		this.endTime = null;
		this.isImportant = isImportant;
	}

	public String toString() {
		if(endTime == null){
			return String.format(TASK_FORMAT, startTime.toString(DATE_FORMAT), "", taskName, isImportant);
		}
		return String.format(TASK_FORMAT, startTime.toString(DATE_FORMAT), endTime.toString(DATE_FORMAT), taskName, isImportant);
	}
	
	public boolean isMatch(String query){
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

	public boolean isImportant() {
		return isImportant;
	}

	public void setImportant(boolean isImportant) {
		this.isImportant = isImportant;
	}
	
	@Override
	public int compareTo(Task otherTask) {
		if(otherTask == null){
			return POSITIVE_NUMBER;
		}
		int compareResult = startTime.compareTo(otherTask.getStartTime());
		if(compareResult != SAME_START_TIMES){
			return compareResult;
		}
		return taskName.compareTo(otherTask.getTaskName());
	}
}
