package storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import utilities.Task;

/**
 * @author a0088278
 * 
 *         The TaskRecords modifies and accesses the text file that is used for
 *         storing task information.
 */
public class TaskRecords {
	private static final String NULL_STRING = "null";
	private static final String EMPTY_STRING = "";
	private static final String NEW_LINE = "\r\n";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern("d/M/yyyy hh:mma");
	private static final String FILE_NAME = "taskrecords.txt";
	private static final String FILE_DELIMITER = "[|]|\r\n";
	private static final Task[] TASK_ARRAY_TYPE = new Task[0];

	private static TaskRecords instanceOfTaskRecords;

	private Task[] currentListOfTasks;
	private TreeSet<Task> allTaskRecords;
	private File myFile;

	/**
	 * Default constructor for TaskRecords. Initializes all task records in text
	 * file to a treeset. Initializes the current list of tasks with today's
	 * upcoming tasks.
	 * 
	 * @throws IOException
	 */
	private TaskRecords() throws IOException {
		myFile = new File(FILE_NAME);
		if (!myFile.exists()) {
			myFile.createNewFile();
		}
		initialiseAllTaskRecords();
	}

	private TaskRecords(String fileName) throws IOException {
		myFile = new File(fileName);
		if (!myFile.exists()) {
			myFile.createNewFile();
		}
		initialiseAllTaskRecords();
	}

	public static TaskRecords getInstance() throws IOException {
		if (instanceOfTaskRecords == null) {
			instanceOfTaskRecords = new TaskRecords();
		}
		return instanceOfTaskRecords;
	}

	public static TaskRecords getInstance(String fileName) throws IOException {
		if (instanceOfTaskRecords == null) {
			instanceOfTaskRecords = new TaskRecords(fileName);
		}
		return instanceOfTaskRecords;
	}

	private void initialiseAllTaskRecords() throws FileNotFoundException {
		allTaskRecords = new TreeSet<Task>();
		Scanner fileReader = new Scanner(myFile);
		fileReader.useDelimiter(FILE_DELIMITER);
		while (fileReader.hasNext()) {
			DateTime startTime = convertStringToDate(fileReader.next());
			DateTime endTime = convertStringToDate(fileReader.next());
			String taskName = fileReader.next();
			Task newTask = new Task(taskName, startTime, endTime);
			allTaskRecords.add(newTask);
		}
		fileReader.close();
	}

	private DateTime convertStringToDate(String stringDate) {
		if (stringDate.isEmpty() || stringDate.equals(NULL_STRING)) {
			return null;
		}
		return DATE_FORMATTER.parseDateTime(stringDate);
	}

	/**
	 * Returns the current list of tasks.
	 * 
	 * @return Task[]
	 */
	public Task[] getCurrentListOfTasks() {
		return currentListOfTasks;
	}

	public Task getTaskByIndex(int index) {
		index--; // Change to zero-based indexing
		if (index < 0 || index >= currentListOfTasks.length) {
			return null;
		}
		return currentListOfTasks[index];
	}

	public Task getTaskByName(String taskName) {
		Task[] taskMatches = findMatchesFromSetOfTasks(allTaskRecords, taskName);
		if (taskMatches.length == 0) {
			return null;
		}
		return taskMatches[0];
	}

	private void rewriteFile() throws IOException {
		FileWriter myFileWriter = new FileWriter(myFile, false);
		Iterator<Task> recordsIterator = allTaskRecords.iterator();
		while (recordsIterator.hasNext()) {
			myFileWriter.write(recordsIterator.next().toString() + NEW_LINE);
		}
		myFileWriter.flush();
		myFileWriter.close();
	}

	/**
	 * Appends the task specified to the text file.
	 * 
	 * @param taskToBeAdded
	 * @return boolean
	 */
	public boolean appendTask(Task taskToBeAdded) {
		boolean isSuccessfullyAdded = allTaskRecords.add(taskToBeAdded);
		FileWriter myFileWriter;
		if (isSuccessfullyAdded) {
			try {
				myFileWriter = new FileWriter(myFile, true);
				myFileWriter.write(taskToBeAdded.toString() + NEW_LINE);
				myFileWriter.flush();
				myFileWriter.close();
			} catch (IOException e) {
				allTaskRecords.remove(taskToBeAdded);
				isSuccessfullyAdded = false;
			}
		}
		return isSuccessfullyAdded;
	}

	/**
	 * Removes the specified task from the text file.
	 * 
	 * @param taskToBeDeleted
	 * @return boolean
	 */
	public boolean deleteTask(Task taskToBeDeleted) {
		try {
			boolean isSuccessfullyDeleted = allTaskRecords
					.remove(taskToBeDeleted);
			if (isSuccessfullyDeleted) {
				try {
					rewriteFile();
				} catch (IOException e) {
					isSuccessfullyDeleted = false;
					allTaskRecords.add(taskToBeDeleted);
				}
			}
			return isSuccessfullyDeleted;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public boolean clearAllTasks() {
		try {
			allTaskRecords.clear();
			rewriteFile();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Replaces the task specified with the second task specified in the text
	 * file.
	 * 
	 * @param taskToBeReplaced
	 * @param newTask
	 * @return boolean
	 */
	public boolean replaceTask(Task taskToBeReplaced, Task newTask) {
		boolean isSuccessfullyDeleted = allTaskRecords.remove(taskToBeReplaced);
		boolean isSuccessfullyAdded = false;
		if (isSuccessfullyDeleted) {
			isSuccessfullyAdded = allTaskRecords.add(newTask);
		}
		if (isSuccessfullyAdded && isSuccessfullyDeleted) {
			try {
				rewriteFile();
			} catch (IOException e) {
				allTaskRecords.remove(newTask);
				allTaskRecords.add(taskToBeReplaced);
				isSuccessfullyAdded = false;
				isSuccessfullyDeleted = false;
			}
		}
		return (isSuccessfullyAdded && isSuccessfullyDeleted);
	}

	public void setCurrentListOfTasks() {
		DateTime toDate = new DateTime().plusDays(1);
		Task fromTask =  new Task(EMPTY_STRING, null, null);
		Task toTask = new Task(toDate);
		currentListOfTasks = allTaskRecords.subSet(fromTask, toTask).toArray(
				TASK_ARRAY_TYPE);
	}

	public void setCurrentListOfTasks(String query) {
		currentListOfTasks = findMatchesFromSetOfTasks(allTaskRecords, query);
	}

	public void setCurrentListOfTasks(DateTime fromDate, DateTime toDate)
			throws IllegalArgumentException {
		if (fromDate.isAfter(toDate)) {
			DateTime tempDate = fromDate;
			fromDate = toDate;
			toDate = tempDate;
		}
		// dummy tasks to facilitate searching in TreeSet
		Task fromTask = new Task(fromDate);
		Task toTask = new Task(toDate);
		currentListOfTasks = allTaskRecords.subSet(fromTask, toTask).toArray(
				TASK_ARRAY_TYPE);
	}

	public void setCurrentListOfTasks(DateTime fromDate) {
		DateTime toDate = fromDate.plusDays(1).toLocalDate()
				.toDateTimeAtStartOfDay();
		// dummy tasks to facilitate searching in TreeSet
		Task fromTask = new Task(fromDate);
		Task toTask = new Task(toDate);
		currentListOfTasks = allTaskRecords.subSet(fromTask, toTask).toArray(
				TASK_ARRAY_TYPE);
	}

	public void setCurrentListOfTasks(String query, DateTime fromDate,
			DateTime toDate) {
		if (fromDate.isAfter(toDate)) {
			DateTime tempDate = fromDate;
			fromDate = toDate;
			toDate = tempDate;
		}
		// dummy tasks to facilitate searching in TreeSet
		Task fromTask = new Task(fromDate);
		Task toTask = new Task(toDate);
		Set<Task> listOfTasksFromSpecifiedDates = allTaskRecords.subSet(
				fromTask, toTask);
		currentListOfTasks = findMatchesFromSetOfTasks(
				listOfTasksFromSpecifiedDates, query);
	}

	public void setCurrentListOfTasks(String query, DateTime fromDate) {
		DateTime toDate = fromDate.plusDays(1).toLocalDate()
				.toDateTimeAtStartOfDay();
		// dummy tasks to facilitate searching in TreeSet
		Task fromTask = new Task(fromDate);
		Task toTask = new Task(toDate);
		Set<Task> listOfTasksFromSpecifiedDates = allTaskRecords.subSet(
				fromTask, toTask);
		currentListOfTasks = findMatchesFromSetOfTasks(
				listOfTasksFromSpecifiedDates, query);
	}

	private Task[] findMatchesFromSetOfTasks(Set<Task> setOfTasks, String query) {
		Iterator<Task> setIterator = setOfTasks.iterator();
		ArrayList<Task> listOfTaskMatches = new ArrayList<Task>();
		while (setIterator.hasNext()) {
			Task currentTask = setIterator.next();
			if (currentTask.isMatch(query)) {
				listOfTaskMatches.add(currentTask);
			}
		}
		return listOfTaskMatches.toArray(TASK_ARRAY_TYPE);
	}
}