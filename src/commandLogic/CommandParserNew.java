package commandLogic;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import utilities.Command;
import utilities.CommandAdd;
import utilities.CommandEdit;
import utilities.CommandMark;
import utilities.CommandRedo;
import utilities.CommandSearch;
import utilities.CommandType;
import utilities.CommandUndo;
import utilities.Task;
import exceptions.CommandCouldNotBeParsedException;

public class CommandParserNew {
	private static final String EMPTY_STRING = "";
	private static final char WHITE_SPACE_CHAR = ' ';
	private static final String MULTIPLE_WHITE_SPACES = "\\s+";
	private static final String WHITE_SPACE = " ";
	private static final int INDEX_OF_FIRST_WORD = 0;

	private static final String[] LIST_ADD_SYNONYMS = { "add", "insert",
			"create", "new", "put" };
	private static final String[] LIST_MARK_SYNONYMS = { "mark", "delete",
			"del", "remove", "discard", "erase", "drop" };
	private static final String[] LIST_EDIT_SYNONYMS = { "update", "edit",
			"change", "alter", "modify" };
	private static final String[] LIST_SEARCH_SYNONYMS = { "find", "display",
			"search", "show" };
	private static final String[] LIST_REDO_SYNONYMS = { "redo" };
	private static final String[] LIST_UNDO_SYNONYMS = { "undo" };

	private static final String PATTERN_DATE = "(\\d{1,2}[-|/]\\d{1,2}[-|/]\\d{2,4})|\\d{1,2}[-|/]\\d{1,2}";
	private static final String PATTERN_DATE_TIME = "\\d{1,2}[/]\\d{1,2}[/]\\d{4} \\d{1,2}[:]\\d{2}";
	private static final String PATTERN_ANY_NUMBER = "\\d";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern("d/M/yyyy H:mm");

	private HashMap<String, CommandType> keywordsDictionary;
	private String commandToParse;

	public CommandParserNew() {
		initialiseDictionary();
	}

	public static void main(String[] args)
			throws CommandCouldNotBeParsedException {
		CommandParserNew cp = new CommandParserNew();
		Command c = cp.parseCommand("something 1-2-2012 1 to 2 add");
		System.out.println(c.toString());
	}

	private void initialiseDictionary() {
		keywordsDictionary = new HashMap<String, CommandType>();
		for (String entry : LIST_ADD_SYNONYMS) {
			keywordsDictionary.put(entry, CommandType.ADD);
		}
		for (String entry : LIST_EDIT_SYNONYMS) {
			keywordsDictionary.put(entry, CommandType.EDIT);
		}
		for (String entry : LIST_MARK_SYNONYMS) {
			keywordsDictionary.put(entry, CommandType.MARK);
		}
		for (String entry : LIST_SEARCH_SYNONYMS) {
			keywordsDictionary.put(entry, CommandType.SEARCH);
		}
		for (String entry : LIST_UNDO_SYNONYMS) {
			keywordsDictionary.put(entry, CommandType.UNDO);
		}
		for (String entry : LIST_REDO_SYNONYMS) {
			keywordsDictionary.put(entry, CommandType.REDO);
		}
	}

	public Command parseCommand(String inputCommand)
			throws CommandCouldNotBeParsedException {
		commandToParse = changeAllDatesToSameFormat(inputCommand);
		try {
			CommandType commandType = getCommandType();
			Command parsedCommand = null;
			switch (commandType) {
			case ADD:
				parsedCommand = parseAdd();
				break;
			case EDIT:
				parsedCommand = parseEdit(commandToParse);
				break;
			case MARK:
				parsedCommand = parseMark(commandToParse);
				break;
			case SEARCH:
				parsedCommand = parseSearch(commandToParse);
				break;
			case UNDO:
				parsedCommand = new CommandUndo();
				break;
			case REDO:
				parsedCommand = new CommandRedo();
				break;
			}
			return parsedCommand;
		} catch (NullPointerException e) {
			throw new CommandCouldNotBeParsedException();
		}

	}

	private String removeExtraWhiteSpaces(String stringToProcess) {
		stringToProcess = stringToProcess.trim().replaceAll(
				MULTIPLE_WHITE_SPACES, WHITE_SPACE);
		return stringToProcess;
	}

	private String changeAllDatesToSameFormat(String stringToProcess) {
		String[] words = stringToProcess.split(WHITE_SPACE);
		for (String word : words) {
			if (word.matches(PATTERN_DATE)) {
				String newDate = convertToMiddleEndian(word.replaceAll("-", "/"));
				stringToProcess = stringToProcess.replace(word, newDate);
			}
		}
		return stringToProcess;
	}

	private String convertToMiddleEndian(String stringToProcess) {
		String[] dateComponents = stringToProcess.split("/");
		String newDate = dateComponents[1] + "/" + dateComponents[0];
		if (dateComponents.length > 2) {
			newDate += "/" + dateComponents[2];
		}
		return newDate;
	}

	private DateTime[] getStartAndEndTimesFromCommand() {
		Parser dateParser = new Parser();
		List<DateGroup> dateGroupList = dateParser.parse(commandToParse);
		DateTime[] startAndEndTime = new DateTime[2];
		if (dateGroupList.isEmpty()) {
			return startAndEndTime;
		}
		while (dateGroupList.size() > 0) {
			DateGroup dateGroup = dateGroupList.get(0);
			if (dateGroup.getText().contains("to")
					| dateGroup.getText().contains(" - ")
					&& dateGroup.getDates().size() > 1) {
				startAndEndTime[0] = new DateTime(dateGroup.getDates().get(0));
				startAndEndTime[1] = new DateTime(dateGroup.getDates().get(1));
				commandToParse = commandToParse
						.replace(dateGroup.getText(), "");
				return startAndEndTime;
			}
			if (dateGroup.getText().contains("for")
					&& dateGroup.getDates().size() > 1) {
				DateTime intervalStart = new DateTime(dateGroup.getDates().get(
						0));
				DateTime intervalEnd = new DateTime(dateGroup.getDates().get(1));
				long intervalDifference = intervalEnd.getMillis()
						- intervalStart.getMillis();
				if (startAndEndTime[0] != null) {
					startAndEndTime[1] = startAndEndTime[0]
							.plus(intervalDifference);
					commandToParse = commandToParse.replace(
							dateGroup.getText(), "");
					return startAndEndTime;
				}
				// reparse to see if there is an alternative start time
				commandToParse = commandToParse
						.replace(dateGroup.getText(), "");
				dateGroupList = dateParser.parse(commandToParse);
				if (dateGroupList.size() > 0) {
					dateGroup = dateGroupList.get(0);
					startAndEndTime[0] = new DateTime(dateGroup.getDates().get(
							0));
					startAndEndTime[1] = startAndEndTime[0]
							.plus(intervalDifference);
				} else {
					startAndEndTime[0] = intervalStart;
					startAndEndTime[1] = intervalEnd;
				}
				commandToParse = commandToParse
						.replace(dateGroup.getText(), "");
				return startAndEndTime;
			}
			if (startAndEndTime[0] == null) {
				startAndEndTime[0] = new DateTime(dateGroup.getDates().get(0));
			} else {
				startAndEndTime[1] = new DateTime(dateGroup.getDates().get(0));
			}
			commandToParse = commandToParse.replace(dateGroup.getText(), "");
			dateGroupList = dateParser.parse(commandToParse);
		}

		return startAndEndTime;
	}

	private CommandAdd parseAdd() throws CommandCouldNotBeParsedException {
		DateTime[] startAndEndTime = getStartAndEndTimesFromCommand();
		String taskName = removeExtraWhiteSpaces(commandToParse);
		if(taskName.length() == 0){
			throw new CommandCouldNotBeParsedException();
		}
		return new CommandAdd(new Task(taskName, startAndEndTime[0],
				startAndEndTime[1]));
	}

	private CommandEdit parseEdit(String command)
			throws CommandCouldNotBeParsedException {
		DateTime startTime = null;
		DateTime endTime = null;
		String taskName = null;
		int taskIndex;
		Pattern datePattern = Pattern.compile(PATTERN_DATE_TIME);
		Pattern anyNumberPattern = Pattern.compile(PATTERN_ANY_NUMBER);
		Matcher patternMatcher = datePattern.matcher(command);
		try {
			if (patternMatcher.find()) {
				startTime = DATE_FORMATTER.parseDateTime(patternMatcher
						.group(0));
				command = command.replaceAll(patternMatcher.group(0),
						EMPTY_STRING);
			}
			patternMatcher = datePattern.matcher(command);
			if (patternMatcher.find()) {
				endTime = DATE_FORMATTER.parseDateTime(patternMatcher.group(0));
				command = command.replaceAll(patternMatcher.group(0),
						EMPTY_STRING);
			}
		} catch (IllegalFieldValueException e) {
			throw new CommandCouldNotBeParsedException();
		}
		patternMatcher = anyNumberPattern.matcher(command);
		if (patternMatcher.find()) {
			taskIndex = Integer.parseInt(patternMatcher.group(0));
			command = command.replaceAll(PATTERN_ANY_NUMBER, EMPTY_STRING);
		} else {
			throw new CommandCouldNotBeParsedException();
		}
		taskName = removeExtraWhiteSpaces(command);
		if (taskName.length() == 0) {
			taskName = null;
		}
		Task newTask = new Task(taskName, startTime, endTime);
		return new CommandEdit(taskIndex, newTask);
	}

	private CommandMark parseMark(String command)
			throws CommandCouldNotBeParsedException {
		Pattern anyNumberPattern = Pattern.compile(PATTERN_ANY_NUMBER);
		Matcher patternMatcher = anyNumberPattern.matcher(command);
		if (patternMatcher.find()) {
			return new CommandMark(Integer.parseInt(patternMatcher.group(0)));
		}
		throw new CommandCouldNotBeParsedException();
	}

	private CommandSearch parseSearch(String command)
			throws CommandCouldNotBeParsedException {
		DateTime startTime = null;
		DateTime endTime = null;
		String taskName = null;
		Pattern datePattern = Pattern.compile(PATTERN_DATE_TIME);
		Matcher patternMatcher = datePattern.matcher(command);
		try {
			if (patternMatcher.find()) {
				startTime = DATE_FORMATTER.parseDateTime(patternMatcher
						.group(0));
				command = command.replaceAll(patternMatcher.group(0),
						EMPTY_STRING);
				System.out.println(startTime);
			}
			patternMatcher = datePattern.matcher(command);
			if (patternMatcher.find()) {
				endTime = DATE_FORMATTER.parseDateTime(patternMatcher.group(0));
				command = command.replaceAll(patternMatcher.group(0),
						EMPTY_STRING);
				System.out.println(endTime);
			}
		} catch (IllegalFieldValueException e) {
			throw new CommandCouldNotBeParsedException();
		}
		taskName = removeExtraWhiteSpaces(command);
		if (taskName.length() == 0) {
			taskName = null;
		}
		return new CommandSearch(taskName, startTime, endTime);
	}

	private CommandType getCommandType()
			throws CommandCouldNotBeParsedException {
		;
		String[] wordsInCommand = commandToParse.split(WHITE_SPACE);
		for (String word : wordsInCommand) {
			if (keywordsDictionary.containsKey(word.toLowerCase())) {
				commandToParse = commandToParse.replace(word, EMPTY_STRING);
				commandToParse = removeExtraWhiteSpaces(commandToParse);
				return keywordsDictionary.get(word.toLowerCase());
			}
		}
		throw new CommandCouldNotBeParsedException();
	}
}
