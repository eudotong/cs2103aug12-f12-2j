package utilities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Task implements Comparable<Task>{
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat
			.forPattern("d/M/yyyy hh:mma");
	private String taskName;
	private DateTime startTime;
	private DateTime endTime;
	private boolean isImportant;
	
	public Task(DateTime startTime){
		this.startTime = startTime;
		taskName = "";
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
			return startTime.toString(DATE_FORMAT) + "||" + taskName + "|" + isImportant;
		}
		return startTime.toString(DATE_FORMAT) + "|"
				+ endTime.toString(DATE_FORMAT) + "|" + taskName + "|" + isImportant;
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
		int compareResult = startTime.compareTo(otherTask.getStartTime());
		if(compareResult != 0){
			return compareResult;
		}
		return taskName.compareTo(otherTask.getTaskName());
	}
}
