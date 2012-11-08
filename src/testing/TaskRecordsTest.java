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
	private static DateTime TODAY = new DateTime();
	ArrayList<Task> tasksToTest;

	@Before
	public void setUp() throws Exception {
		taskRecordsTest = TaskRecords.getInstance("testing.txt");
		taskRecordsTest.clearAllTasks();
		tasksToTest = new ArrayList<Task>();
		tasksToTest.add(new Task("A First Task", null, null));
		tasksToTest.add(new Task("B Second Task", TODAY.minusDays(2), null));
		tasksToTest.add(new Task("C 3rd Task", TODAY.minusDays(1), null));
		tasksToTest.add(new Task("D FoUrTh Task", TODAY.minusHours(1), null));
		tasksToTest.add(new Task("e fifth Task", TODAY, null));
		tasksToTest.add(new Task("f sixth Task", TODAY.plusMinutes(1), null));
		tasksToTest.add(new Task("G 7th Task", TODAY.plusDays(1), null));
		tasksToTest.add(new Task("H 8th Task", TODAY.plusDays(10), null));
		for (Task task : tasksToTest) {
			taskRecordsTest.appendTask(task);
		}
	}

	@Test
	public void testGetCurrentListOfTasks() {
		resetCurrentList();
		assertEquals(tasksToTest.toArray(new Task[0]),
				taskRecordsTest.getCurrentListOfTasks());
		// Test case: current list of tasks set
		taskRecordsTest.setCurrentListOfTasks("No matching yay yay!");
		assertEquals(new Task[0], taskRecordsTest.getCurrentListOfTasks());
	}

	@Test
	public void testGetTaskByIndex() {
		resetCurrentList();
		// Test case: task index less than 1
		assertEquals(null, taskRecordsTest.getTaskByIndex(0));
		// Test case: task index more than length of list
		assertEquals(null, taskRecordsTest.getTaskByIndex(100));
		// Test case: task index valid
		assertEquals(tasksToTest.get(2), taskRecordsTest.getTaskByIndex(3));
	}

	@Test
	public void testGetTaskByName() {
		// TODO
	}

	@Test
	public void testAppendTask() {
		Task newTask = new Task("random", null, null);
		// Test case: can add
		assertTrue(taskRecordsTest.appendTask(newTask));
		resetCurrentList();
		assertEquals(9, taskRecordsTest.getCurrentListOfTasks().length);
		assertEquals(newTask, taskRecordsTest.getTaskByIndex(2));
		// Test case: cannot add
		assertFalse(taskRecordsTest.appendTask(newTask));
		resetCurrentList();
		assertEquals(9, taskRecordsTest.getCurrentListOfTasks().length);
	}

	@Test
	public void testDeleteTask() {
		Task invalidTaskToDelete = new Task("random", null, null);
		// Test case: cannot delete
		assertFalse(taskRecordsTest.deleteTask(invalidTaskToDelete));
		resetCurrentList();
		assertEquals(tasksToTest.toArray(new Task[0]),
				taskRecordsTest.getCurrentListOfTasks());
		// Test case: can delete
		assertTrue(taskRecordsTest.deleteTask(tasksToTest.remove(0)));
		resetCurrentList();
		assertEquals(tasksToTest.toArray(new Task[0]),
				taskRecordsTest.getCurrentListOfTasks());
	}

	@Test
	public void testReplaceTask() {
		Task invalidTask = new Task("random", null, null);
		Task newTask = new Task("New Task", null, null);
		// Test case: cannot replace (taskToBeReplaced not found)
		assertFalse(taskRecordsTest.replaceTask(invalidTask, newTask));
		resetCurrentList();
		assertEquals(tasksToTest.toArray(new Task[0]),
				taskRecordsTest.getCurrentListOfTasks());
		// Test case: cannot replace (newTask already exists)
		assertFalse(taskRecordsTest.replaceTask(tasksToTest.get(0),
				tasksToTest.get(1)));
		resetCurrentList();
		assertEquals(tasksToTest.toArray(new Task[0]),
				taskRecordsTest.getCurrentListOfTasks());
		// Test case: can replace
		assertTrue(taskRecordsTest.replaceTask(tasksToTest.get(0), newTask));
		resetCurrentList();
		assertEquals(newTask, taskRecordsTest.getTaskByIndex(1));
	}

	@Test
	public void testRemoveAll() {
		Task[] tasksToRemoveInvalid = { tasksToTest.get(0),
				new Task("random", null, null) };
		// Test case: some in array not matching
		tasksToTest.remove(0);
		assertTrue(taskRecordsTest.removeAll(tasksToRemoveInvalid));
		resetCurrentList();
		assertEquals(tasksToTest.toArray(new Task[0]),
				taskRecordsTest.getCurrentListOfTasks());
		//Test case: all matching
		assertTrue(taskRecordsTest.removeAll(tasksToTest.toArray(new Task[0])));
		resetCurrentList();
		assertEquals(new Task[0], taskRecordsTest.getCurrentListOfTasks());
	}

	@Test
	public void testSetCurrentListOfTasks() {
		Task [] expectedList = {tasksToTest.get(0), tasksToTest.get(5), tasksToTest.get(6), tasksToTest.get(7)};
		taskRecordsTest.setCurrentListOfTasks();
		assertEquals(expectedList, taskRecordsTest.getCurrentListOfTasks());
	}
	
	@Test
	public void testSetCurrentListOfTasksString() {
		Task [] expectedList = {tasksToTest.get(3), tasksToTest.get(4), tasksToTest.get(5), tasksToTest.get(6), tasksToTest.get(7)};
		taskRecordsTest.setCurrentListOfTasks("th");
		assertEquals(expectedList, taskRecordsTest.getCurrentListOfTasks());
		taskRecordsTest.setCurrentListOfTasks("Th");
		assertEquals(expectedList, taskRecordsTest.getCurrentListOfTasks());
		taskRecordsTest.setCurrentListOfTasks("");
		assertEquals(tasksToTest.toArray(new Task[0]), taskRecordsTest.getCurrentListOfTasks());
	}

	@Test
	public void testSetCurrentListOfTasksDateTimeDateTime() {
		Task [] expectedList = {tasksToTest.get(4), tasksToTest.get(5), tasksToTest.get(6)};
		taskRecordsTest.setCurrentListOfTasks(TODAY, TODAY.plusDays(2));
		assertEquals(expectedList, taskRecordsTest.getCurrentListOfTasks());
	}

	@Test
	public void testSetCurrentListOfTasksDateTime() {
		Task [] expectedList = {tasksToTest.get(4), tasksToTest.get(5)};
		taskRecordsTest.setCurrentListOfTasks(TODAY);
		assertEquals(expectedList, taskRecordsTest.getCurrentListOfTasks());
	}

	@Test
	public void testSetCurrentListOfTasksStringDateTimeDateTime() {
		Task [] expectedList = {tasksToTest.get(3)};
		taskRecordsTest.setCurrentListOfTasks("th", TODAY.minusDays(2), TODAY);
		assertEquals(expectedList, taskRecordsTest.getCurrentListOfTasks());
	}

	@Test
	public void testSetCurrentListOfTasksStringDateTime() {
		Task [] expectedList = {tasksToTest.get(4), tasksToTest.get(5)};
		taskRecordsTest.setCurrentListOfTasks("th", TODAY);
		assertEquals(expectedList, taskRecordsTest.getCurrentListOfTasks());
	}

	private void resetCurrentList() {
		taskRecordsTest.setCurrentListOfTasks("");
	}

}
