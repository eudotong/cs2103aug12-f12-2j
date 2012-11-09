package testing;


import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Test;

import commandLogic.CommandParser;
import exceptions.CommandCouldNotBeParsedException;
import exceptions.StartTimeAfterEndTimeException;

import utilities.Command;
import utilities.CommandType;
import utilities.Task;

//Note that sometimes comparing dates are tricky! If there is an error tr running it a second time.
public class CommandParserTest {
	private static final String EMPTY_STRING = "";
	private static final String NULL = "null";
	private static final DateTime IMPOSSIBLY_LARGE_DATE = new DateTime(
			Long.MAX_VALUE);
	private static final DateTime IMPOSSIBLY_SMALL_DATE = new DateTime(
			Long.MIN_VALUE);
	private static DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern("dd/MM/yyyy HH:mm");
	private static final String IMPOSSIBLY_SMALL_DATE_STRING = IMPOSSIBLY_SMALL_DATE
			.toString(DATE_FORMATTER);
	private static final String IMPOSSIBLY_LARGE_DATE_STRING = IMPOSSIBLY_LARGE_DATE
			.toString(DATE_FORMATTER);
	private static DateTime NOV_6 = DATE_FORMATTER
			.parseDateTime("06/11/2012 10:00");
	private static final DateTime START_OF_NOV_6 = NOV_6.withTimeAtStartOfDay();
	private static final String START_OF_NOV_6_STRING = START_OF_NOV_6
			.toString(DATE_FORMATTER);
	private static DateTime TODAY = new DateTime();
	private static DateTime START_OF_TODAY = new DateTime()
			.withTimeAtStartOfDay();
	private static final String START_OF_TODAY_STRING = START_OF_TODAY
			.toString(DATE_FORMATTER);
	private static CommandParser COMMAND_PARSER = new CommandParser();

	private DateTime getNextMonday() {
		int daysToAdd = (1 + 7 - START_OF_TODAY.getDayOfWeek());
		return START_OF_TODAY.plusDays(daysToAdd);
	}

	@Test
	public void testParseCommand() throws CommandCouldNotBeParsedException,
			StartTimeAfterEndTimeException {
		getNextMonday();
		Command actualCommand;
		Task expectedTask = new Task("name", TODAY, TODAY.plusHours(1));
		// Add Command
		// Test cases: "for" keyword
		actualCommand = COMMAND_PARSER.parseCommand("add now for 1 hour name");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		actualCommand = COMMAND_PARSER
				.parseCommand("add name now for 60 minutes");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		expectedTask = new Task("name teehee teehee", START_OF_TODAY,
				START_OF_TODAY.plusHours(2));
		actualCommand = COMMAND_PARSER
				.parseCommand("add name teehee teehee today for 2 hrs");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		expectedTask = new Task("something add", NOV_6, NOV_6.plusHours(1));
		actualCommand = COMMAND_PARSER
				.parseCommand("add something add 6 nov 2012 10am for 1 hour");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		// Test cases: "to" or "-" keyword
		actualCommand = COMMAND_PARSER
				.parseCommand("add 6/11/12 10.00am to 11am something add");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		expectedTask = new Task("name", START_OF_NOV_6, START_OF_TODAY);
		actualCommand = COMMAND_PARSER
				.parseCommand("add 6/11/12 to today name");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		// Test cases: only start time
		expectedTask = new Task("important task man", NOV_6, null);
		actualCommand = COMMAND_PARSER
				.parseCommand("add important task man 6/11/12 10:00");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		actualCommand = COMMAND_PARSER
				.parseCommand("add important task man 6/11/12 10.00");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		actualCommand = COMMAND_PARSER
				.parseCommand("add important task man 6.11.12 10am");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		// Test cases: start and end time
		expectedTask = new Task("important task man", NOV_6, NOV_6.plusHours(1)
				.plusMinutes(30));
		actualCommand = COMMAND_PARSER
				.parseCommand("add important task man 6/11/12 10:00 6/11/12 11:30");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		// Test cases: no times
		expectedTask = new Task("Juney's wedding with fries", null, null);
		actualCommand = COMMAND_PARSER
				.parseCommand("add Juney's wedding with fries");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		// Test cases: special date monday
		expectedTask = new Task("something good", getNextMonday(), null);
		actualCommand = COMMAND_PARSER
				.parseCommand("add monday something good");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		expectedTask = new Task("something good", getNextMonday(), null);
		actualCommand = COMMAND_PARSER.parseCommand("add mon something good");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		expectedTask = new Task("m", getNextMonday(), null);
		actualCommand = COMMAND_PARSER.parseCommand("add mon m");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		actualCommand = COMMAND_PARSER.parseCommand("add monday m");
		assertEquals(expectedTask.toString(), actualCommand.toString());
		
		// Edit Command
		// Test cases: edit name only
		expectedTask = new Task("new name", null, null);
		actualCommand = COMMAND_PARSER.parseCommand("edit 1 new name");
		assertEquals(1 + expectedTask.toString(), actualCommand.toString());
		// Test cases: edit name and start time
		expectedTask = new Task("new name", START_OF_TODAY, null);
		actualCommand = COMMAND_PARSER.parseCommand("edit 1 new name today");
		assertEquals(1 + expectedTask.toString(), actualCommand.toString());
		// Test cases: edit name and start time and end time
		expectedTask = new Task("new name", NOV_6, NOV_6.plusHours(1));
		actualCommand = COMMAND_PARSER
				.parseCommand("edit 1 new name 6 nov 10am to 11am");
		assertEquals(1 + expectedTask.toString(), actualCommand.toString());

		// Mark Command
		actualCommand = COMMAND_PARSER.parseCommand("mark 1");
		assertEquals("1", actualCommand.toString());
		actualCommand = COMMAND_PARSER.parseCommand("del 1 asdasd");
		assertEquals("1", actualCommand.toString());

		// Search Command
		// Test cases: only query
		actualCommand = COMMAND_PARSER.parseCommand("search keyword");
		assertEquals("keywordnullnull", actualCommand.toString());
		// Test cases: only from date
		actualCommand = COMMAND_PARSER.parseCommand("find 6th november");
		assertEquals(EMPTY_STRING + START_OF_NOV_6_STRING + NULL,
				actualCommand.toString());
		// Test cases: only from date and to date
		actualCommand = COMMAND_PARSER
				.parseCommand("find 6th november to today");
		assertEquals(EMPTY_STRING + START_OF_NOV_6_STRING
				+ START_OF_TODAY_STRING, actualCommand.toString());
		// Test cases: query and from date
		actualCommand = COMMAND_PARSER
				.parseCommand("search keyword 6 nov 2012");
		assertEquals("keyword" + START_OF_NOV_6_STRING + NULL,
				actualCommand.toString());
		// Test cases: query and from date and to date
		actualCommand = COMMAND_PARSER
				.parseCommand("search keyword 6th nov 2012 to now");
		assertEquals(
				"keyword" + START_OF_NOV_6_STRING
						+ new DateTime().toString(DATE_FORMATTER),
				actualCommand.toString());
		// Test cases: search before
		actualCommand = COMMAND_PARSER.parseCommand("search before 6/11/2012");
		assertEquals(EMPTY_STRING + IMPOSSIBLY_SMALL_DATE_STRING
				+ START_OF_NOV_6_STRING, actualCommand.toString());
		// Test cases: search after
		actualCommand = COMMAND_PARSER.parseCommand("search after 6/11/2012");
		assertEquals(EMPTY_STRING + START_OF_NOV_6_STRING
				+ IMPOSSIBLY_LARGE_DATE_STRING, actualCommand.toString());
		// Test cases: search all
		actualCommand = COMMAND_PARSER.parseCommand("search all");
		assertEquals(EMPTY_STRING + NULL + NULL, actualCommand.toString());
		// Test cases: search before with query
		actualCommand = COMMAND_PARSER
				.parseCommand("search keyword before 6/11/2012");
		assertEquals("keyword" + IMPOSSIBLY_SMALL_DATE_STRING
				+ START_OF_NOV_6_STRING, actualCommand.toString());
		// Test cases: search after with query
		actualCommand = COMMAND_PARSER
				.parseCommand("search after 6/11/2012 keyword");
		assertEquals("keyword" + START_OF_NOV_6_STRING
				+ IMPOSSIBLY_LARGE_DATE_STRING, actualCommand.toString());

		// Mark All Command
		actualCommand = COMMAND_PARSER.parseCommand("mark all");
		assertEquals(CommandType.MARK_ALL, actualCommand.getCommandType());
		actualCommand = COMMAND_PARSER.parseCommand("del everything");
		assertEquals(CommandType.MARK_ALL, actualCommand.getCommandType());

	}

}
