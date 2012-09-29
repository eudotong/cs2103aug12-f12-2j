import java.util.Date;
public class Task {
	private String taskName;
	private Date taskDate;
	private String category;
	private boolean isImportant;
	private int lineNumber;
	public Task(String taskName, Date taskDate, String category, boolean isImportant, int lineNumber) {
		super();
		this.taskName = taskName;
		this.taskDate = taskDate;
		this.category = category;
		this.isImportant = isImportant;
		this.lineNumber = lineNumber;
	}
	public String getTaskName() {
		return taskName;
	}
	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}
	public Date getTaskDate() {
		return taskDate;
	}
	public void setTaskDate(Date taskDate) {
		this.taskDate = taskDate;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public boolean isImportant() {
		return isImportant;
	}
	public void setImportant(boolean isImportant) {
		this.isImportant = isImportant;
	}
	public int getLineNumber() {
		return lineNumber;
	}
	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}
}
