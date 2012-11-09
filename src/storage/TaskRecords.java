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
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import utilities.Task;

/**
 * @author A0088278L
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
	private static Logger logger = Logger.getLogger("JIMI");

	private Task[] currentListOfTasks;
	private TreeSet<Task> allTaskRecords;
	private File myFile;

	/**
	 * Initializes all task records in text
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

	/**
	 * Returns instance of TaskRecords.
	 * 
	 * @return TaskRecords
	 * @throws IOException
	 */
	public static TaskRecords getInstance() throws IOException {
		if (instanceOfTaskRecords == null) {
			instanceOfTaskRecords = new TaskRecords();
		}
		return instanceOfTaskRecords;
	}

	/**
	 * Returns instance of TaskRecords.
	 * 
	 * @return TaskRecords
	 * @throws IOException
	 */
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

	/**
	 * Gets task from the currentListOfTask by its index. Null is returned if
	 * index is invalid.
	 * 
	 * @param index
	 * @return Task
	 */
	public Task getTaskByIndex(int index) {
		index--; // Change to zero-based indexing
		if (index < 0 || index >= currentListOfTasks.length) {
			return null;
		}
		return currentListOfTasks[index];
	}

	// TODO either remove or use
	/*
	 * public Task getTaskByName(String taskName) { Task[] taskMatches =
	 * findMatchesFromSetOfTasks(allTaskRecords, taskName); if
	 * (taskMatches.length == 0) { return null; } return taskMatches[0]; }
	 */

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
		logger.log(Level.INFO,
				"Appending task \"" + taskToBeAdded.getTaskName()
						+ "\" to task records.");
		boolean isSuccessfullyAdded = allTaskRecords.add(taskToBeAdded);
		FileWriter myFileWriter;
		if (isSuccessfullyAdded) {
			try {
				myFileWriter = new FileWriter(myFile, true);
				myFileWriter.write(taskToBeAdded.toString() + NEW_LINE);
				myFileWriter.flush();
				myFileWriter.close();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error: could not write to file.");
				/*
				 * allTaskRecords.remove(taskToBeAdded); isSuccessfullyAdded =
				 * false;
				 */
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
		logger.log(Level.INFO,
				"Deleting task \"" + taskToBeDeleted.getTaskName()
						+ "\" from task records.");
		try {
			boolean isSuccessfullyDeleted = allTaskRecords
					.remove(taskToBeDeleted);
			if (isSuccessfullyDeleted) {
				try {
					rewriteFile();
				} catch (IOException e) {
					logger.log(Level.SEVERE, "Error: could not write to file.");
					/*
					 * isSuccessfullyDeleted = false;
					 * allTaskRecords.add(taskToBeDeleted);
					 */
				}
			}
			return isSuccessfullyDeleted;
		} catch (NullPointerException e) {
			return false;
		}
	}

	/**
	 * Adds all tasks in array to the text file.
	 * 
	 * @param tasksToAdd
	 */
	public void addAll(Task[] tasksToAdd) {
		logger.log(Level.INFO, "Appending a list of tasks to task records.");
		for (Task task : tasksToAdd) {
			appendTask(task);
		}
	}

	/**
	 * Deleted all tasks in array from the text file.
	 * 
	 * @param tasksToDelete
	 */
	// TODO ASK ABOUT IO EXCEPTION
	public void removeAll(Task[] tasksToDelete) {
		logger.log(Level.INFO, "Deleting a list of tasks from task records.");
		ArrayList<Task> tasksSuccessfullyRemoved = new ArrayList<Task>();
		for (Task task : tasksToDelete) {
			if (allTaskRecords.remove(task)) {
				tasksSuccessfullyRemoved.add(task);
			}
		}
		try {
			rewriteFile();
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error: could not write to file.");
			/*
			 * for (Task task : tasksSuccessfullyRemoved) {
			 * allTaskRecords.add(task); }
			 */
		}
	}

	/**
	 * Clears all tasks in the task records. Method is used for testing.
	 * 
	 * @return boolean
	 */
	public boolean clearAllTasks() {
		try {
			allTaskRecords.clear();
			rewriteFile();
			return true;
		} catch (IOException e) {
			logger.log(Level.SEVERE, "Error: could not write to file.");
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
		logger.log(Level.INFO,
				"Replacing task \"" + taskToBeReplaced.getTaskName()
						+ "\" with \"" + newTask.getTaskName()
						+ "\"in task records.");
		boolean isSuccessfullyDeleted = allTaskRecords.remove(taskToBeReplaced);
		boolean isSuccessfullyAdded = false;
		if (isSuccessfullyDeleted) {
			isSuccessfullyAdded = allTaskRecords.add(newTask);
			if (!isSuccessfullyAdded) {
				allTaskRecords.add(taskToBeReplaced);
			}
		}
		if (isSuccessfullyAdded && isSuccessfullyDeleted) {
			try {
				rewriteFile();
			} catch (IOException e) {
				logger.log(Level.SEVERE, "Error: could not write to file.");
				allTaskRecords.remove(newTask);
				allTaskRecords.add(taskToBeReplaced);
				isSuccessfullyAdded = false;
				isSuccessfullyDeleted = false;
			}
		}
		return (isSuccessfullyAdded && isSuccessfullyDeleted);
	}

	/**
	 * Sets current list of tasks to default. The default list contains all
	 * floating tasks and upcoming tasks.
	 */
	public void setCurrentListOfTasks() {
		logger.log(
				Level.INFO,
				"Setting current list of tasks to default (null tasks and tasks later than now)");
		DateTime today = new DateTime();
		Task fromTask = new Task(today);
		ArrayList<Task> upcomingTasks = new ArrayList<Task>();
		Iterator<Task> taskIterator = allTaskRecords.iterator();
		while (taskIterator.hasNext()) {
			Task task = taskIterator.next();
			if (task.getStartTime() == null) {
				upcomingTasks.add(task);
			}
		}
		upcomingTasks.addAll(allTaskRecords.tailSet(fromTask));
		currentListOfTasks = upcomingTasks.toArray(TASK_ARRAY_TYPE);
	}

	/**
	 * Sets the current list of tasks to tasks that match query specified.
	 * 
	 * @param query
	 */
	public void setCurrentListOfTasks(String query) {
		logger.log(Level.INFO,
				"Setting current list of tasks matching query \"" + query
						+ "\"");
		if (query == null) {
			query = EMPTY_STRING;
		}
		currentListOfTasks = findMatchesFromSetOfTasks(allTaskRecords, query);
	}

	/**
	 * Sets the current list of tasks to tasks that match query, from date and
	 * to date specified.
	 * 
	 * @param query
	 * @param fromDate
	 * @param toDate
	 */
	public void setCurrentListOfTasks(String query, DateTime fromDate,
			DateTime toDate) {
		logger.log(Level.INFO, "Setting current list of tasks matching \""
				+ query + "\" from " + fromDate.toString(DATE_FORMATTER)
				+ " to " + toDate.toString(DATE_FORMATTER));
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
		if (query.isEmpty()) {
			currentListOfTasks = listOfTasksFromSpecifiedDates
					.toArray(TASK_ARRAY_TYPE);
			return;
		}
		currentListOfTasks = findMatchesFromSetOfTasks(
				listOfTasksFromSpecifiedDates, query);
	}

	/**
	 * Sets the current list of tasks to tasks that match query, from date
	 * specified.
	 * 
	 * @param query
	 * @param fromDate
	 */
	public void setCurrentListOfTasks(String query, DateTime fromDate) {
		logger.log(Level.INFO, "Setting current list of tasks matching \""
				+ query + "\" from " + fromDate.toString(DATE_FORMATTER));
		DateTime toDate = fromDate.plusDays(1).toLocalDate()
				.toDateTimeAtStartOfDay();
		// dummy tasks to facilitate searching in TreeSet
		Task fromTask = new Task(fromDate);
		Task toTask = new Task(toDate);
		Set<Task> listOfTasksFromSpecifiedDates = allTaskRecords.subSet(
				fromTask, toTask);
		if (query.isEmpty()) {
			currentListOfTasks = listOfTasksFromSpecifiedDates
					.toArray(TASK_ARRAY_TYPE);
			return;
		}
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