package commandLogic;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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

public class CommandParserSimple {
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
	private static final String PATTERN_DATE = "\\d{1,2}[, - / .]\\d{1,2}[, - / .]\\d{4} \\d{1,2}[:]\\d{2}";
	private static final String PATTERN_ANY_NUMBER = "\\d";
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormat
			.forPattern("d/M/yyyy H:mm");
	private HashMap<String, CommandType> dictionary;

	public CommandParserSimple() {
		initialiseDictionary();
	}

	public static void main(String[] args) {
		CommandParserSimple cps = new CommandParserSimple();
		try {
			cps.parseCommand("add something 14/1/2012 00:00");
		} catch (CommandCouldNotBeParsedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
			String[] wordsInCommand = command.split(" ");
			System.out.println(command);
			CommandType commandType = getCommandType(wordsInCommand[0]);
			command = removeFirstWord(command);
			System.out.println(command);
			System.out.println(commandType);
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

	private String removeFirstWord(String command) {
		return command.substring(command.indexOf(" ") + 1);
	}

	private String removeExtraWhiteSpaces(String command) {
		command = command.replaceAll("\\s+", " ");
		if (command.length() != 0
				&& command.charAt(command.length() - 1) == ' ') {
			command = command.substring(0, command.length() - 1);
		}
		if (command.length() != 0 && command.charAt(0) == ' ') {
			command = command.substring(1);
		}
		if (command.length() == 0) {
			return null;
		}
		return command;
	}

	private CommandAdd parseAdd(String command) {
		DateTime startTime = new DateTime();
		DateTime endTime = null;
		String taskName = "";
		Pattern datePattern = Pattern.compile(PATTERN_DATE);
		Matcher patternMatcher = datePattern.matcher(command);
		if (patternMatcher.find()) {
			startTime = DATE_FORMATTER.parseDateTime(patternMatcher.group(0));
			if (patternMatcher.groupCount() > 1) {
				endTime = DATE_FORMATTER.parseDateTime(patternMatcher.group(1));
			}
			command = command.replaceAll(PATTERN_DATE, "");
		}
		taskName = removeExtraWhiteSpaces(command);
		System.out.println(taskName);
		Task newTask = new Task(taskName, startTime, endTime, false);
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
		if (patternMatcher.find()) {
			startTime = DATE_FORMATTER.parseDateTime(patternMatcher.group(0));
			if (patternMatcher.groupCount() > 1) {
				endTime = DATE_FORMATTER.parseDateTime(patternMatcher.group(1));
			}
			command = command.replaceAll(PATTERN_DATE, "");
		}
		patternMatcher = anyNumberPattern.matcher(command);
		if (patternMatcher.find()) {
			taskIndex = Integer.parseInt(patternMatcher.group(0));
			command = command.replaceAll(PATTERN_ANY_NUMBER, "");
		} else {
			throw new CommandCouldNotBeParsedException();
		}
		taskName = removeExtraWhiteSpaces(command);
		Task newTask = new Task(taskName, startTime, endTime, false);
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

	private CommandSearch parseSearch(String command) {
		DateTime startTime = null;
		DateTime endTime = null;
		String taskName = null;
		Pattern datePattern = Pattern.compile(PATTERN_DATE);
		Matcher patternMatcher = datePattern.matcher(command);
		if (patternMatcher.find()) {
			startTime = DATE_FORMATTER.parseDateTime(patternMatcher.group(0));
			if (patternMatcher.groupCount() > 1) {
				endTime = DATE_FORMATTER.parseDateTime(patternMatcher.group(1));
			}
			command = command.replaceAll(PATTERN_DATE, "");
		}
		taskName = removeExtraWhiteSpaces(command);
		return new CommandSearch(taskName, startTime, endTime);
	}

	public CommandType getCommandType(String command)
			throws CommandCouldNotBeParsedException {
		if (dictionary.containsKey(command)) {
			return dictionary.get(command);
		}
		throw new CommandCouldNotBeParsedException();
	}
}
