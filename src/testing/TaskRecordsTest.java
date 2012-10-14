package testing;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import storage.TaskRecords;
import utilities.Task;

public class TaskRecordsTest {
	TaskRecords taskRecordsTest;
	DateTime date = new DateTime();
	ArrayList <Task> tasksToTest = new ArrayList<Task>();
	@Before
	public void setUp() throws Exception {
		taskRecordsTest = new TaskRecords("testing.txt");
		taskRecordsTest.clearAllTasks();
		tasksToTest.add(new Task("AAA 2 days before", date.minusDays(2), true));
		tasksToTest.add(new Task("AAA 1 days before", date.minusDays(1), true));
		tasksToTest.add(new Task("BBB 1 day after", date.plusDays(1), true));
		tasksToTest.add(new Task("CCC 3 hours later", date.minusHours(3), true));
		tasksToTest.add(new Task("DDD 4 months later", date.plusMonths(4), true));
		tasksToTest.add(new Task("EEE 5 months", date.plusWeeks(2), true));
		tasksToTest.add(new Task("Aaa 1 hour later", date.plusHours(1), true));
		tasksToTest.add(new Task("ABC 1 yr later", date.plusYears(1), true));
		tasksToTest.add(new Task("CBA now", date, true));
		tasksToTest.add(new Task("QWERTY 10 hours later", date.plusHours(10), true));
		tasksToTest.add(new Task("abc 3 days", date.plusDays(3), true));
		tasksToTest.add(new Task("ghj 3 days", date.plusDays(3), true));
		tasksToTest.add(new Task("ghjasd 3 days", date.plusDays(3), true));
		tasksToTest.add(new Task("gasdhj 3 days", date.plusDays(3), true));
		for(Task t : tasksToTest){
			taskRecordsTest.appendTask(t);
		}
	}

	@Test
	public void testGetTaskByIndex() {
		taskRecordsTest.setCurrentListOfTasks(date, date.plusDays(1));
		Task taskByIndex = taskRecordsTest.getTaskByIndex(1);
		assertEquals(tasksToTest.get(8), taskByIndex);
		taskByIndex = taskRecordsTest.getTaskByIndex(3);
		assertEquals(tasksToTest.get(9), taskByIndex);
		taskByIndex = taskRecordsTest.getTaskByIndex(10);
		assertEquals(null, taskByIndex);
	}

	@Test
	public void testGetTaskByName() {
		taskRecordsTest.setCurrentListOfTasks(date, date.plusDays(1));
		Task taskByName = taskRecordsTest.getTaskByName("AAA");
		assertEquals(tasksToTest.get(0), taskByName);
		taskByName = taskRecordsTest.getTaskByName("eE");
		assertEquals(tasksToTest.get(5), taskByName);
		taskByName = taskRecordsTest.getTaskByName("not inside");
		assertEquals(null, taskByName);
	}

	@Test
	public void testDeleteTask() {
		taskRecordsTest.deleteTask(new Task("CBA now", date, true));
		taskRecordsTest.setCurrentListOfTasks(date);
		Task taskByIndex = taskRecordsTest.getTaskByIndex(1);
		assertEquals(tasksToTest.get(6), taskByIndex);
		taskRecordsTest.deleteTask(new Task("CBA now", date, true));
		taskRecordsTest.setCurrentListOfTasks(date);
		taskByIndex = taskRecordsTest.getTaskByIndex(1);
		assertEquals(tasksToTest.get(6), taskByIndex);
		assertFalse(taskRecordsTest.deleteTask(null));
	}

	@Test
	public void testReplaceTask() {
		Task changeToTask =new Task("changed", date, true);
		taskRecordsTest.replaceTask(new Task("CBA now", date, true), changeToTask);
		taskRecordsTest.setCurrentListOfTasks(date);
		Task taskByIndex = taskRecordsTest.getTaskByIndex(1);
		assertEquals(changeToTask, taskByIndex);
		//Test case: Task does not exist
		taskRecordsTest.replaceTask(new Task("CBA now", date, true), changeToTask);
		taskRecordsTest.setCurrentListOfTasks(date);
		taskByIndex = taskRecordsTest.getTaskByIndex(1);
		assertEquals(changeToTask, taskByIndex);
	}

	@Test
	public void testSetCurrentListOfTasks() {
		taskRecordsTest.setCurrentListOfTasks(date.plusYears(1));
		Task taskByIndex = taskRecordsTest.getTaskByIndex(1);
		assertEquals(tasksToTest.get(7), taskByIndex);
		taskRecordsTest.setCurrentListOfTasks("QWERTY");
		taskByIndex = taskRecordsTest.getTaskByIndex(1);
		assertEquals(tasksToTest.get(9), taskByIndex);
		taskRecordsTest.setCurrentListOfTasks(date.minusDays(1), date);
		taskByIndex = taskRecordsTest.getTaskByIndex(1);
		assertEquals(tasksToTest.get(1), taskByIndex);
		taskRecordsTest.setCurrentListOfTasks("gh", date.plusDays(3));
		taskByIndex = taskRecordsTest.getTaskByIndex(1);
		assertEquals(tasksToTest.get(11), taskByIndex);
		taskRecordsTest.setCurrentListOfTasks(date.minusDays(1));
		taskByIndex = taskRecordsTest.getTaskByIndex(1);
		assertEquals(tasksToTest.get(1), taskByIndex);
	}

}
