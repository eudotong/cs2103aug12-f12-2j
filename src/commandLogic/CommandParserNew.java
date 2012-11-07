package commandLogic;

import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;

import utilities.Command;
import utilities.CommandAdd;
import utilities.CommandEdit;
import utilities.CommandMark;
import utilities.CommandMarkAll;
import utilities.CommandRedo;
import utilities.CommandSearch;
import utilities.CommandType;
import utilities.CommandUndo;
import utilities.Task;
import exceptions.CommandCouldNotBeParsedException;
import exceptions.StartTimeAfterEndTimeException;

public class CommandParserNew {
	private static final String KEYWORD_FOR = "for";
	private static final String KEYWORD_DASH = " - ";
	private static final String KEYWORD_TO = " to ";
	private static final String KEYWORD_NOW = "now";
	private static final int END_TIME = 1;
	private static final int START_TIME = 0;
	private static final int NUM_START_AND_END_TIMES = 2;
	private static final int POSITIVE_NUMBER = 1;
	private static final int SAME_TIME = 0;
	private static final int NEGATIVE_NUMBER = -1;
	private static final String SLASH = "/";
	private static final String DASH = "-";
	private static final int MILLISECOND_DIFFERENCE_ALLOWANCE = 120;
	private static final String EMPTY_STRING = "";
	private static final String MULTIPLE_WHITE_SPACES = "\\s+";
	private static final String WHITE_SPACE = " ";

	private static final String[] LIST_ADD_SYNONYMS = { "add", "insert",
			"create", "new", "put" };
	private static final String[] LIST_MARK_SYNONYMS = { "mark", "delete",
			"del", "remove", "discard", "erase", "drop" };
	private static final String[] LIST_EDIT_SYNONYMS = { "update", "edit",
			"change", "alter", "modify" };
	private static final String[] LIST_SEARCH_SYNONYMS = { "find", "display",
			"search", "show", "view", "lookup" };
	private static final String[] LIST_ALL_SYNONYMS = { "all", "everything" };
	private static final String[] LIST_REDO_SYNONYMS = { "redo" };
	private static final String[] LIST_UNDO_SYNONYMS = { "undo" };

	private static final String PATTERN_DATE = "(\\d{1,2}[-|/]\\d{1,2}[-|/]\\d{2,4})|\\d{1,2}[-|/]\\d{1,2}";
	private static final String PATTERN_ANY_NUMBER = "\\d+";
	private static final String PATTERN_ANY_NUMBER_NOT_DATE = "(^| )\\d+($| )";

	private static Logger logger = Logger.getLogger("JIMI");

	private HashMap<String, CommandType> keywordsDictionary;
	private String commandToParse;

	public CommandParserNew() {
		initialiseDictionary();
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
		for (String entry : LIST_ALL_SYNONYMS) {
			keywordsDictionary.put(entry, CommandType.MARK_ALL);
		}
	}

	public Command parseCommand(String inputCommand)
			throws CommandCouldNotBeParsedException, StartTimeAfterEndTimeException {
		commandToParse = changeAllDatesToSameFormat(inputCommand);
		try {
			CommandType commandType = getCommandType();
			Command parsedCommand = null;
			switch (commandType) {
			case ADD:
				parsedCommand = parseAdd();
				break;
			case EDIT:
				parsedCommand = parseEdit();
				break;
			case MARK:
				parsedCommand = parseMark(commandToParse);
				break;
			case SEARCH:
				parsedCommand = parseSearch();
				break;
			case UNDO:
				parsedCommand = new CommandUndo();
				break;
			case REDO:
				parsedCommand = new CommandRedo();
				break;
			case MARK_ALL:
				parsedCommand = new CommandMarkAll();
				break;
			}
			return parsedCommand;
		} catch (NullPointerException e) {
			throw new CommandCouldNotBeParsedException();
		}
	}

	private String removeExtraWhiteSpaces(String stringToProcess) {
		return stringToProcess.trim().replaceAll(MULTIPLE_WHITE_SPACES,
				WHITE_SPACE);
	}

	private String changeAllDatesToSameFormat(String stringToProcess) {
		String[] words = stringToProcess.split(WHITE_SPACE);
		for (String word : words) {
			if (word.matches(PATTERN_DATE)) {
				String newDate = convertToMiddleEndian(word.replaceAll(DASH,
						SLASH));
				stringToProcess = stringToProcess.replace(word, newDate);
			}
		}
		return stringToProcess;
	}

	private String convertToMiddleEndian(String stringToProcess) {
		String[] dateComponents = stringToProcess.split(SLASH);
		String newDate = dateComponents[1] + SLASH + dateComponents[0];
		if (dateComponents.length > 2) {
			newDate += SLASH + dateComponents[2];
		}
		return newDate;
	}

	// TODO get rid of the error of parsing substring "wedding", "fries" etc.
	private DateTime[] getStartAndEndTimesFromCommand() {
		Parser dateParser = new Parser();
		List<DateGroup> dateGroupList = dateParser.parse(commandToParse);
		DateTime[] startAndEndTime = new DateTime[NUM_START_AND_END_TIMES];
		if (dateGroupList.isEmpty()) {
			return startAndEndTime;
		}
		while (dateGroupList.size() > 0) {
			DateGroup dateGroup = dateGroupList.get(0);
			logger.log(Level.INFO, "Parsing Date: " + dateGroup.getText());
			if ((dateGroup.getText().contains(KEYWORD_TO) | dateGroup.getText()
					.contains(KEYWORD_DASH)) && dateGroup.getDates().size() > 1) {
				startAndEndTime[START_TIME] = new DateTime(dateGroup.getDates()
						.get(0));
				startAndEndTime[END_TIME] = new DateTime(dateGroup.getDates()
						.get(1));
				if (!dateGroup.getText().contains(KEYWORD_NOW)) {
					if (!isTimeSpecified(startAndEndTime[START_TIME])) {
						startAndEndTime[START_TIME] = startAndEndTime[START_TIME]
								.withTimeAtStartOfDay();
					}
					if (!isTimeSpecified(startAndEndTime[END_TIME])) {
						startAndEndTime[END_TIME] = startAndEndTime[END_TIME]
								.withTimeAtStartOfDay();

					}
					commandToParse = commandToParse.replace(
							dateGroup.getText(), EMPTY_STRING);
					return startAndEndTime;
				}
			}
			if (dateGroup.getText().contains(KEYWORD_FOR)
					&& dateGroup.getDates().size() > 1) {
				DateTime intervalStart = new DateTime(dateGroup.getDates().get(
						0));
				DateTime intervalEnd = new DateTime(dateGroup.getDates().get(1));
				long intervalDifference = intervalEnd.getMillis()
						- intervalStart.getMillis();
				if (startAndEndTime[START_TIME] != null) {
					startAndEndTime[END_TIME] = startAndEndTime[START_TIME]
							.plus(intervalDifference);
					commandToParse = commandToParse.replace(
							dateGroup.getText(), EMPTY_STRING);
					return startAndEndTime;
				}
				// reparse to see if there is an alternative start time
				commandToParse = commandToParse.replace(dateGroup.getText(),
						EMPTY_STRING);
				dateGroupList = dateParser.parse(commandToParse);
				if (dateGroupList.size() > 0) {
					dateGroup = dateGroupList.get(0);
					startAndEndTime[START_TIME] = new DateTime(dateGroup
							.getDates().get(0));
					startAndEndTime[END_TIME] = startAndEndTime[START_TIME]
							.plus(intervalDifference);
				} else {
					startAndEndTime[START_TIME] = intervalStart;
					startAndEndTime[END_TIME] = intervalEnd;
				}
				commandToParse = commandToParse.replace(dateGroup.getText(),
						EMPTY_STRING);
				return startAndEndTime;
			}
			if (startAndEndTime[START_TIME] == null) {
				startAndEndTime[START_TIME] = new DateTime(dateGroup.getDates()
						.get(0));
				if (!dateGroup.getText().contains(KEYWORD_NOW)
						&& !isTimeSpecified(startAndEndTime[START_TIME])) {
					startAndEndTime[START_TIME] = startAndEndTime[START_TIME]
							.withTimeAtStartOfDay();
				}
			} else {
				startAndEndTime[END_TIME] = new DateTime(dateGroup.getDates().get(0));
			}
			commandToParse = commandToParse.replace(dateGroup.getText(), EMPTY_STRING);
			dateGroupList = dateParser.parse(commandToParse);
		}

		return startAndEndTime;
	}

	private boolean isTimeSpecified(DateTime dateTimeToCheck) {
		int timeNow = new DateTime().getMillisOfDay();
		int timeSpecified = dateTimeToCheck.getMillisOfDay();
		if (Math.abs(timeNow - timeSpecified) <= MILLISECOND_DIFFERENCE_ALLOWANCE) {
			return false;
		}
		return true;
	}

	private CommandAdd parseAdd() throws CommandCouldNotBeParsedException, StartTimeAfterEndTimeException {
		logger.log(Level.INFO, "Parsing as add command.");
		DateTime[] startAndEndTime = getStartAndEndTimesFromCommand();
		if(compareNullDatesLast(startAndEndTime[START_TIME], startAndEndTime[END_TIME]) > SAME_TIME){
			throw new StartTimeAfterEndTimeException();
		}
		String taskName = removeExtraWhiteSpaces(commandToParse);
		if (taskName.length() == 0) {
			throw new CommandCouldNotBeParsedException();
		}
		return new CommandAdd(new Task(taskName, startAndEndTime[0],
				startAndEndTime[1]));
	}

	private CommandEdit parseEdit()
			throws CommandCouldNotBeParsedException, StartTimeAfterEndTimeException {
		logger.log(Level.INFO, "Parsing as edit command.");
		String taskName = null;
		int taskIndex;
		Pattern anyNumberPattern = Pattern.compile(PATTERN_ANY_NUMBER_NOT_DATE);
		Matcher patternMatcher = anyNumberPattern.matcher(commandToParse);
		if (patternMatcher.find()) {
			taskIndex = Integer.parseInt(patternMatcher.group(0).trim());
			System.out.println(taskIndex);
			if (taskIndex <= 0) {
				throw new CommandCouldNotBeParsedException();
			}
			commandToParse = commandToParse.replaceAll(PATTERN_ANY_NUMBER_NOT_DATE,
					EMPTY_STRING);
		} else {
			throw new CommandCouldNotBeParsedException();
		}
		DateTime[] startAndEndTime = getStartAndEndTimesFromCommand();
		if(compareNullDatesLast(startAndEndTime[START_TIME], startAndEndTime[END_TIME]) > SAME_TIME){
			throw new StartTimeAfterEndTimeException();
		}
		taskName = removeExtraWhiteSpaces(commandToParse);
		Task newTask = new Task(taskName, startAndEndTime[START_TIME],
				startAndEndTime[END_TIME]);
		return new CommandEdit(taskIndex, newTask);
	}

	private CommandMark parseMark(String command)
			throws CommandCouldNotBeParsedException {
		logger.log(Level.INFO, "Parsing as mark command.");
		Pattern anyNumberPattern = Pattern.compile(PATTERN_ANY_NUMBER);
		Matcher patternMatcher = anyNumberPattern.matcher(command);
		if (patternMatcher.find()) {
			return new CommandMark(Integer.parseInt(patternMatcher.group(0)));
		}
		throw new CommandCouldNotBeParsedException();
	}

	private CommandSearch parseSearch() throws CommandCouldNotBeParsedException, StartTimeAfterEndTimeException {
		logger.log(Level.INFO, "Parsing as search command.");
		DateTime[] startAndEndTime = getStartAndEndTimesFromCommand();
		if(compareNullDatesLast(startAndEndTime[START_TIME], startAndEndTime[END_TIME]) > SAME_TIME){
			throw new StartTimeAfterEndTimeException();
		}
		commandToParse = removeExtraWhiteSpaces(commandToParse);
		if (commandToParse.length() == 0) {
			commandToParse = null;
		}
		return new CommandSearch(commandToParse, startAndEndTime[START_TIME],
				startAndEndTime[END_TIME]);
	}
	
	private int compareNullDatesLast(DateTime firstDate, DateTime secondDate) {
		if (firstDate == null && secondDate == null) {
			return SAME_TIME;
		}
		if (firstDate == null && secondDate != null) {
			return POSITIVE_NUMBER;
		}
		if (firstDate != null && secondDate == null) {
			return NEGATIVE_NUMBER;
		}
		return firstDate.compareTo(secondDate);
	}

	private CommandType getCommandType()
			throws CommandCouldNotBeParsedException {
		logger.log(Level.INFO, "Getting command type.");
		String[] wordsInCommand = commandToParse.split(WHITE_SPACE);
		CommandType commandType = null;
		int indexOfWord = 0;
		for (String word : wordsInCommand) {
			if (keywordsDictionary.containsKey(word.toLowerCase())) {
				commandToParse = commandToParse.replace(word, EMPTY_STRING);
				commandToParse = removeExtraWhiteSpaces(commandToParse);
				commandType = keywordsDictionary.get(word.toLowerCase());
			}
			indexOfWord++;
		}
		int indexOfNextWord = indexOfWord + 1;
		if (commandType == CommandType.MARK
				&& indexOfNextWord < wordsInCommand.length) {
			if (keywordsDictionary.get(wordsInCommand[indexOfNextWord]) == CommandType.MARK_ALL) {
				return CommandType.MARK_ALL;
			}
		}
		if (commandType == null) {
			throw new CommandCouldNotBeParsedException();
		}
		return commandType;
	}
}
