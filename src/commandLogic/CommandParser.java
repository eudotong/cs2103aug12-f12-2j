package commandLogic;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

public class CommandParser {
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
	
	private static final String PATTERN_DATE = "\\d{1,2}[/]\\d{1,2}[/]\\d{4} \\d{1,2}[:]\\d{2}";
	private static final String PATTERN_ANY_NUMBER = "\\d";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern("d/M/yyyy H:mm");
	
	private HashMap<String, CommandType> dictionary;

	public CommandParser() {
		initialiseDictionary();
	}

	private void initialiseDictionary() {
		dictionary = new HashMap<String, CommandType>();
		for (String entry : LIST_ADD_SYNONYMS) {
			dictionary.put(entry, CommandType.ADD);
		}
		for (String entry : LIST_EDIT_SYNONYMS) {
			dictionary.put(entry, CommandType.EDIT);
		}
		for (String entry : LIST_MARK_SYNONYMS) {
			dictionary.put(entry, CommandType.MARK);
		}
		for (String entry : LIST_SEARCH_SYNONYMS) {
			dictionary.put(entry, CommandType.SEARCH);
		}
		for (String entry : LIST_UNDO_SYNONYMS) {
			dictionary.put(entry, CommandType.UNDO);
		}
		for (String entry : LIST_REDO_SYNONYMS) {
			dictionary.put(entry, CommandType.REDO);
		}
	}

	public Command parseCommand(String command)
			throws CommandCouldNotBeParsedException {
		try {
			command = removeExtraWhiteSpaces(command);
			String[] wordsInCommand = command.split(WHITE_SPACE);
			CommandType commandType = getCommandType(wordsInCommand[INDEX_OF_FIRST_WORD]);
			command = removeFirstWord(command);
			Command parsedCommand = null;
			switch (commandType) {
			case ADD:
				parsedCommand = parseAdd(command);
				break;
			case EDIT:
				parsedCommand = parseEdit(command);
				break;
			case MARK:
				parsedCommand = parseMark(command);
				break;
			case SEARCH:
				parsedCommand = parseSearch(command);
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

	private String removeFirstWord(String inputString) {
		return inputString.substring(inputString.indexOf(WHITE_SPACE) + 1);
	}

	private String removeExtraWhiteSpaces(String inputString) {
		inputString = inputString
				.replaceAll(MULTIPLE_WHITE_SPACES, WHITE_SPACE);
		inputString = removeLeadingWhiteSpace(inputString);
		inputString = removeTrailingWhiteSpace(inputString);
		return inputString;
	}

	private String removeTrailingWhiteSpace(String inputString) {
		if (inputString.length() != 0
				&& inputString.charAt(0) == WHITE_SPACE_CHAR) {
			inputString = inputString.substring(1);
		}
		return inputString;
	}

	private String removeLeadingWhiteSpace(String inputString) {
		if (inputString.length() != 0
				&& inputString.charAt(inputString.length() - 1) == WHITE_SPACE_CHAR) {
			inputString = inputString.substring(0, inputString.length() - 1);
		}
		return inputString;
	}

	private CommandAdd parseAdd(String command)
			throws CommandCouldNotBeParsedException {
		DateTime startTime = null;
		DateTime endTime = null;
		String taskName = EMPTY_STRING;
		Pattern datePattern = Pattern.compile(PATTERN_DATE);
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
		taskName = removeExtraWhiteSpaces(command);
		Task newTask = new Task(taskName, startTime, endTime);
		return new CommandAdd(newTask);
	}

	private CommandEdit parseEdit(String command)
			throws CommandCouldNotBeParsedException {
		DateTime startTime = null;
		DateTime endTime = null;
		String taskName = null;
		int taskIndex;
		Pattern datePattern = Pattern.compile(PATTERN_DATE);
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
		Pattern datePattern = Pattern.compile(PATTERN_DATE);
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

	private CommandType getCommandType(String command)
			throws CommandCouldNotBeParsedException {
		String commandInLowerCase = command.toLowerCase();
		if (dictionary.containsKey(commandInLowerCase)) {
			return dictionary.get(commandInLowerCase);
		}
		throw new CommandCouldNotBeParsedException();
	}
}
