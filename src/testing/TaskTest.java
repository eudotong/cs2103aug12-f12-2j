package testing;

import static org.junit.Assert.*;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Before;
import org.junit.Test;

import utilities.Task;

public class TaskTest {
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern("d/M/yyyy hh:mma");

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testTaskDateTime() {
		DateTime testDate = new DateTime();
		Task testTask = new Task(testDate);
		assertEquals(testDate, testTask.getStartTime());
		assertEquals(null, testTask.getEndTime());
		assertEquals("", testTask.getTaskName());
	}

	@Test
	public void testTaskStringDateTimeDateTime() {
		DateTime earlierTime = new DateTime();
		DateTime laterTime = new DateTime().plusHours(1);
		String name = "arbitrary name";
		String invalidName = "arbitrary| name";
		Task testTask;
		// Test case: not a valid name (name has a "|")
		testTask = new Task(invalidName, earlierTime, laterTime);
		assertEquals(name, testTask.getTaskName());
		assertEquals(earlierTime, testTask.getStartTime());
		assertEquals(laterTime, testTask.getEndTime());
		// Test case: not a valid name (name is null)
		testTask = new Task(null, earlierTime, laterTime);
		assertEquals("", testTask.getTaskName());
		assertEquals(earlierTime, testTask.getStartTime());
		assertEquals(laterTime, testTask.getEndTime());
		// Test case: start time before end time
		testTask = new Task(name, earlierTime, laterTime);
		assertEquals(name, testTask.getTaskName());
		assertEquals(earlierTime, testTask.getStartTime());
		assertEquals(laterTime, testTask.getEndTime());
		// Test case: start time after end time
		testTask = new Task(name, laterTime, earlierTime);
		assertEquals(name, testTask.getTaskName());
		assertEquals(earlierTime, testTask.getStartTime());
		assertEquals(laterTime, testTask.getEndTime());
	}

	@Test
	public void testToString() {
		DateTime startTime = DATE_FORMATTER.parseDateTime("15/10/2012 01:00PM");
		DateTime endTime = DATE_FORMATTER.parseDateTime("15/10/2012 02:00PM");
		Task testTask;
		String name = "arbitrary name";
		// Test Case: dates are null
		testTask = new Task(name, null, null);
		assertEquals("null|null|" + name, testTask.toString());
		// Test Case: dates are not null
		testTask = new Task(name, startTime, endTime);
		assertEquals("15/10/2012 01:00PM|15/10/2012 02:00PM|" + name,
				testTask.toString());
	}

	@Test
	public void testIsMatch() {
		String name = "arbitrary name";
		Task testTask = new Task(name, null, null);
		// Test case: does not match
		assertFalse(testTask.isMatch("not matching"));
		// Test case: match substring
		assertTrue(testTask.isMatch("name"));
		// Test case: match substring not case-sensitive
		assertTrue(testTask.isMatch("NaMe"));
		// Test case: match whole string
		assertTrue(testTask.isMatch("arbitrary name"));
		// Test case: matching whole string not case-sensitive
		assertTrue(testTask.isMatch("arbitrary NAME"));
	}

	@Test
	public void testCompareTo() {
		Task test1;
		Task test2;
		String name1 = "arb name";
		String name2 = "arb name yup";
		DateTime date1 = DATE_FORMATTER.parseDateTime("15/10/2012 01:00PM");
		DateTime date2 = DATE_FORMATTER.parseDateTime("15/10/2012 02:00PM");

		// Test case: one task is null
		test1 = new Task(name1, null, null);
		assertTrue(test1.compareTo(null) < 0);

		// Group of test cases (where name is the same)
		// Test case: both null start dates
		test1 = new Task(name1, null, null);
		test2 = new Task(name1, null, null);
		assertTrue(test1.compareTo(test2) == 0);
		assertTrue(test2.compareTo(test1) == 0);
		// Test case: test1 null start date, test2 not null start date
		test1 = new Task(name1, null, null);
		test2 = new Task(name1, date1, null);
		assertTrue(test1.compareTo(test2) > 0);
		assertTrue(test2.compareTo(test1) < 0);
		// Test case: test1 not null start date, test2 null start date
		test2 = new Task(name1, null, null);
		test1 = new Task(name1, date1, null);
		assertTrue(test2.compareTo(test1) > 0);
		assertTrue(test1.compareTo(test2) < 0);
		// Test case: both not null start dates that are different
		test2 = new Task(name1, date2, null);
		test1 = new Task(name1, date1, null);
		assertTrue(test2.compareTo(test1) > 0);
		assertTrue(test1.compareTo(test2) < 0);
		// Test case: both not null start dates that are same
		test2 = new Task(name1, date1, null);
		test1 = new Task(name1, date1, null);
		assertTrue(test2.compareTo(test1) == 0);
		assertTrue(test1.compareTo(test2) == 0);

		// Group of test cases (where name is not the same)
		// Test case: both null start dates
		test1 = new Task(name1, null, null);
		test2 = new Task(name2, null, null);
		assertTrue(test1.compareTo(test2) < 0);
		assertTrue(test2.compareTo(test1) > 0);
		// Test case: test1 null start date, test2 not null start date
		test1 = new Task(name1, null, null);
		test2 = new Task(name2, date1, null);
		assertTrue(test1.compareTo(test2) > 0);
		assertTrue(test2.compareTo(test1) < 0);
		// Test case: test1 not null start date, test2 null start date
		test2 = new Task(name1, null, null);
		test1 = new Task(name2, date1, null);
		assertTrue(test2.compareTo(test1) > 0);
		assertTrue(test1.compareTo(test2) < 0);
		// Test case: both not null start dates that are different
		test2 = new Task(name2, date2, null);
		test1 = new Task(name1, date1, null);
		assertTrue(test2.compareTo(test1) > 0);
		assertTrue(test1.compareTo(test2) < 0);
		// Test case: both not null start dates that are same
		test2 = new Task(name2, date1, null);
		test1 = new Task(name1, date1, null);
		assertTrue(test2.compareTo(test1) > 0);
		assertTrue(test1.compareTo(test2) < 0);
	}

}
