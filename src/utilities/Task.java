package utilities;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Task implements Comparable<Task>{
	private String taskName;
	private DateTime startTime;
	private DateTime endTime;
	private boolean isImportant;
	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormat
			.forPattern("dd/MM/yyyy hh:mma");

	public Task(DateTime startTime){
		this.startTime = startTime;
		taskName = "";
		isImportant = false;
	}
	public Task(String taskName, DateTime startTime, DateTime endTime,
			boolean isImportant) {
		if(startTime.isAfter(endTime)){
			this.startTime = endTime;
			this.endTime = startTime;
		}else{
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
			return startTime.toString(DATE_FORMAT) + "||" + taskName + "|" + isImportant + "\r\n";
		}
		return startTime.toString(DATE_FORMAT) + "|"
				+ endTime.toString(DATE_FORMAT) + "|" + taskName + "|" + isImportant + "\r\n";
	}
	
	public boolean isMatch(String query){
		return taskName.contains(query);
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
