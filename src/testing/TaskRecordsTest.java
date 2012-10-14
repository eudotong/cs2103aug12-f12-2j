package testing;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import storage.TaskRecords;
import utilities.Task;

public class TaskRecordsTest {
	TaskRecords taskRecordsTest;
	DateTime date = new DateTime();
	Task [] tasksToTest = new Task[10];
	@Before
	public void setUp() throws Exception {
		taskRecordsTest = new TaskRecords("testing.txt");
		taskRecordsTest.clearAllTasks();
		tasksToTest[0] = new Task("AAA 2 days before", date.minusDays(2), true);
		tasksToTest[1] = new Task("AAA 1 days before", date.minusDays(1), true);
		tasksToTest[2] = new Task("BBB 1 day after", date.plusDays(1), true);
		tasksToTest[3] = new Task("CCC 3 hours later", date.minusHours(3), true);
		tasksToTest[4] = new Task("DDD 4 months later", date.plusMonths(4), true);
		tasksToTest[5] = new Task("EEE 5 months", date.plusWeeks(2), true);
		tasksToTest[6] = new Task("Aaa 1 hour later", date.plusHours(1), true);
		tasksToTest[7] = new Task("ABC 1 yr later", date.plusYears(1), true);
		tasksToTest[8] = new Task("CBA now", date, true);
		tasksToTest[9] = new Task("QWERTY 10 hours later", date.plusHours(10), true);
		taskRecordsTest.appendTask(tasksToTest[0]);
		taskRecordsTest.appendTask(tasksToTest[1]);
		taskRecordsTest.appendTask(tasksToTest[2]);
		taskRecordsTest.appendTask(tasksToTest[3]);
		taskRecordsTest.appendTask(tasksToTest[4]);
		taskRecordsTest.appendTask(tasksToTest[5]);
		taskRecordsTest.appendTask(tasksToTest[6]);
		taskRecordsTest.appendTask(tasksToTest[7]);
		taskRecordsTest.appendTask(tasksToTest[8]);
		taskRecordsTest.appendTask(tasksToTest[9]);
	}

	@Test
	public void testGetTaskByIndex() {
		taskRecordsTest.setCurrentListOfTasks(date);
		Task taskByIndex = taskRecordsTest.getTaskByIndex(1);
		assertEquals(tasksToTest[8], taskByIndex);
		taskByIndex = taskRecordsTest.getTaskByIndex(3);
		assertEquals(tasksToTest[9], taskByIndex);
		taskByIndex = taskRecordsTest.getTaskByIndex(10);
		assertEquals(null, taskByIndex);
	}

	@Test
	public void testGetTaskByName() {
		taskRecordsTest.setCurrentListOfTasks(date);
		Task taskByName = taskRecordsTest.getTaskByName("AAA");
		assertEquals(tasksToTest[0], taskByName);
		taskByName = taskRecordsTest.getTaskByName("eE");
		assertEquals(tasksToTest[5], taskByName);
		taskByName = taskRecordsTest.getTaskByName("not inside");
		assertEquals(null, taskByName);
	}

	@Test
	public void testAppendTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeleteTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testReplaceTask() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetCurrentListOfTasksString() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetCurrentListOfTasksDateTimeDateTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetCurrentListOfTasksDateTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetCurrentListOfTasksStringDateTimeDateTime() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetCurrentListOfTasksStringDateTime() {
		fail("Not yet implemented");
	}

}
