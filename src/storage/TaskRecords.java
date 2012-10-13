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

import utility.Task;

/**
 * @author a0088278
 * 
 *         The TaskRecords modifies and accesses the text file that is used for
 *         storing task information.
 */
public class TaskRecords {
	private static final String DATE_FORMAT = "d/M/yyyy h:mma";
	private static final String FILE_NAME = "taskrecords.txt";
	private static final String FILE_DELIMITER = "[|]|\r\n";
	private Task[] currentListOfTasks;
	private TreeSet<Task> allTaskRecords;
	private File myFile;

	// Tester please delete later
	public static void main(String[] args) throws IOException {
		TaskRecords tr = new TaskRecords();
		DateTime dt = new DateTime().plusHours(1);
		tr.appendTask(new Task("hello", dt, true));
		tr = new TaskRecords();
		Task[] tasks = tr.getCurrentListOfTasks();
		for (Task t : tasks) {
			System.out.print(t.toString());
		}
		System.out.println("-Search day b4------");
		DateTimeFormatter formatter = DateTimeFormat
				.forPattern("d/M/yyyy h:mma");
		DateTime date = formatter.parseDateTime("12/10/2012 10:21PM");
		/*
		 * //Test delete and replace tr.deleteTask(new Task("c", date, true));
		 * tr.replaceTask(new Task("erqe", date, true), new Task("c", date,
		 * true)); tr = new TaskRecords(); tasks = tr.getCurrentListOfTasks();
		 * for (Task t : tasks) { System.out.print(t.toString()); }
		 */
		// test search
		tr.setCurrentListOfTasks(new DateTime().minusDays(1));
		tasks = tr.getCurrentListOfTasks();
		for (Task t : tasks) {
			System.out.print(t.toString());
		}
		System.out.println("-search btw ytd tmr------");
		tr.setCurrentListOfTasks(new DateTime().minusDays(1),
				new DateTime().plusDays(1));
		tasks = tr.getCurrentListOfTasks();
		for (Task t : tasks) {
			System.out.print(t.toString());
		}
		System.out.println("-search hel ytd------");
		tr.setCurrentListOfTasks("hel", new DateTime().minusDays(1));
		tasks = tr.getCurrentListOfTasks();
		for (Task t : tasks) {
			System.out.print(t.toString());
		}
		System.out.println("-------");
	}

	/**
	 * Default constructor for TaskRecords. Initializes all task records in text
	 * file to a treeset. Initializes the current list of tasks with today's
	 * upcoming tasks.
	 * 
	 * @throws IOException
	 */
	public TaskRecords() throws IOException {
		myFile = new File(FILE_NAME);
		if (!myFile.exists()) {
			myFile.createNewFile();
		}
		initialiseAllTaskRecords();
		initialiseCurrentListOfTasks();
	}

	private void initialiseCurrentListOfTasks() {
		DateTime now = new DateTime();
		DateTime startOfTomorrow = now.plusDays(1).toLocalDate()
				.toDateTimeAtStartOfDay();
		setCurrentListOfTasks(now, startOfTomorrow);
	}

	private void initialiseAllTaskRecords() throws FileNotFoundException {
		allTaskRecords = new TreeSet<Task>();
		Scanner fileReader = new Scanner(myFile);
		fileReader.useDelimiter(FILE_DELIMITER);
		while (fileReader.hasNext()) {
			DateTime startTime = convertStringToDate(fileReader.next());
			DateTime endTime = convertStringToDate(fileReader.next());
			String taskName = fileReader.next();
			boolean isImportant = Boolean.parseBoolean(fileReader.next());
			Task newTask = new Task(taskName, startTime, endTime, isImportant);
			allTaskRecords.add(newTask);
		}
		fileReader.close();
	}

	private DateTime convertStringToDate(String stringDate) {
		if (stringDate.isEmpty()) {
			return null;
		}
		DateTimeFormatter formatter = DateTimeFormat.forPattern(DATE_FORMAT);
		return formatter.parseDateTime(stringDate);
	}

	/**
	 * Returns the current list of tasks.
	 * 
	 * @return Task[]
	 */
	public Task[] getCurrentListOfTasks() {
		return currentListOfTasks;
	}

	public Task getTaskByIndex(int index) throws ArrayIndexOutOfBoundsException {
		return currentListOfTasks[index];
	}

	private void rewriteFile() throws IOException {
		FileWriter myFileWriter = new FileWriter(myFile, false);
		Iterator<Task> recordsIterator = allTaskRecords.iterator();
		while (recordsIterator.hasNext()) {
			myFileWriter.write(recordsIterator.next().toString());
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
				myFileWriter.write(taskToBeAdded.toString());
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
		boolean isSuccessfullyDeleted = allTaskRecords.remove(taskToBeDeleted);
		if (isSuccessfullyDeleted) {
			try {
				rewriteFile();
			} catch (IOException e) {
				isSuccessfullyDeleted = false;
				allTaskRecords.add(taskToBeDeleted);
			}
		}
		return isSuccessfullyDeleted;
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

	public void setCurrentListOfTasks(String query) {
		currentListOfTasks = findMatchesFromSetOfTasks(allTaskRecords, query);
	}

	public void setCurrentListOfTasks(DateTime fromDate, DateTime toDate)
			throws IllegalArgumentException {
		// dummy tasks to facilitate searching in TreeSet
		Task fromTask = new Task(fromDate);
		Task toTask = new Task(toDate);
		currentListOfTasks = allTaskRecords.subSet(fromTask, toTask).toArray(
				new Task[1]);
	}

	public void setCurrentListOfTasks(DateTime fromDate) {
		DateTime toDate = fromDate.plusDays(1).toLocalDate()
				.toDateTimeAtStartOfDay();
		// dummy tasks to facilitate searching in TreeSet
		Task fromTask = new Task(fromDate);
		Task toTask = new Task(toDate);
		currentListOfTasks = allTaskRecords.subSet(fromTask, toTask).toArray(
				new Task[1]);
	}

	public void setCurrentListOfTasks(String query, DateTime fromDate,
			DateTime toDate) {
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
		return listOfTaskMatches.toArray(new Task[1]);
	}
}
