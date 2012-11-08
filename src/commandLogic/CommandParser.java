package commandLogic;

import java.util.HashMap;
import java.util.HashSet;
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

/**
 * 
 * @author A0088278L
 * 
 *         The CommandParser takes in a command as a String and returns a
 *         Command object accordingly.
 */
public class CommandParser {
	private static final String COLON = ":";
	private static final String DOT = ".";
	private static final int FIRST_VALID_INDEX = 1;
	private static final int FIRST_GROUP = 0;
	private static final int START_INDEX = 0;
	private static final int NEXT_WORD = 1;
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

	private static final DateTime IMPOSSIBLY_LARGE_DATE = new DateTime(
			Long.MAX_VALUE);
	private static final DateTime IMPOSSIBLY_SMALL_DATE = new DateTime(
			Long.MIN_VALUE);

	private static final char[] LIST_DISALLOWED_START_CHARS = { 'a', 'e', 'p',
			's', 'd', 'f', 'h', 'l', 'z', 'm' };
	private static final String[] LIST_SHORT_DATE_VARIANTS = { "mon", "tues",
			"wed", "thurs", "fri", "sat", "sun", "jan", "feb", "mar", "apr",
			"may", "jun", "jul", "aug", "sep", "oct", "nov", "dec" };
	private static final String[] LIST_DATE_VARIANTS = { "am", "pm", "day",
			"days", "hour", "hours", "hr", "hrs" };
	private static final String[] LIST_ADD_SYNONYMS = { "add", "insert",
			"create", "new", "put", "ins" };
	private static final String[] LIST_MARK_SYNONYMS = { "mark", "delete",
			"del", "remove", "discard", "erase", "drop", "clear" };
	private static final String[] LIST_EDIT_SYNONYMS = { "update", "edit",
			"change", "alter", "modify" };
	private static final String[] LIST_SEARCH_SYNONYMS = { "find", "display",
			"search", "show", "view", "lookup" };
	private static final String[] LIST_ALL_SYNONYMS = { "all", "everything" };
	private static final String[] LIST_REDO_SYNONYMS = { "redo" };
	private static final String[] LIST_UNDO_SYNONYMS = { "undo" };
	private static final String[] LIST_RELATIVE_TIME_KEYWORDS_BEFORE = {
			"before", "b4", "bef" };
	private static final String[] LIST_RELATIVE_TIME_KEYWORDS_AFTER = {
			"after", "aft" };

	private static final String PATTERN_TIME = "(\\d{1,4}|\\d{1,2}[:|.]\\d{2})(am|pm)";
	private static final String PATTERN_ALPHANUMERIC_WORD = "\\w*";
	private static final String PATTERN_TIME_DOT_SEPARATOR = "\\d{1,2}.\\d{2}";
	private static final String PATTERN_DATE = "(\\d{1,2}[-|/]\\d{1,2}[-|/]\\d{2,4})|\\d{1,2}[-|/]\\d{1,2}";
	private static final String PATTERN_NON_DIGIT = "\\D+";
	private static final String PATTERN_NUMBER = "\\d+";
	private static final String PATTERN_ANY_NUMBER_NOT_DATE = "(^| )\\d+($| )";

	private static Logger logger = Logger.getLogger("JIMI");

	private HashMap<String, CommandType> commandTypeKeywordsDictionary;
	private HashMap<String, CommandType> specialKeywordsDictionary;
	private HashMap<String, RelativeType> relativeTimeKeywordsDictionary;
	private HashSet<String> dateVariantsDictionary;
	private HashSet<Character> disallowedStartCharsDictionary;

	private String commandToParse;

	public CommandParser() {
		initialiseDictionaries();
	}

	private void initialiseDictionaries() {
		commandTypeKeywordsDictionary = new HashMap<String, CommandType>();
		specialKeywordsDictionary = new HashMap<String, CommandType>();
		relativeTimeKeywordsDictionary = new HashMap<String, RelativeType>();
		dateVariantsDictionary = new HashSet<String>();
		disallowedStartCharsDictionary = new HashSet<Character>();
		for (char entry : LIST_DISALLOWED_START_CHARS) {
			disallowedStartCharsDictionary.add(entry);
		}
		for (String entry : LIST_DATE_VARIANTS) {
			dateVariantsDictionary.add(entry);
		}
		for (String entry : LIST_ADD_SYNONYMS) {
			commandTypeKeywordsDictionary.put(entry, CommandType.ADD);
		}
		for (String entry : LIST_EDIT_SYNONYMS) {
			commandTypeKeywordsDictionary.put(entry, CommandType.EDIT);
		}
		for (String entry : LIST_MARK_SYNONYMS) {
			commandTypeKeywordsDictionary.put(entry, CommandType.MARK);
		}
		for (String entry : LIST_SEARCH_SYNONYMS) {
			commandTypeKeywordsDictionary.put(entry, CommandType.SEARCH);
		}
		for (String entry : LIST_UNDO_SYNONYMS) {
			commandTypeKeywordsDictionary.put(entry, CommandType.UNDO);
		}
		for (String entry : LIST_REDO_SYNONYMS) {
			commandTypeKeywordsDictionary.put(entry, CommandType.REDO);
		}
		for (String entry : LIST_ALL_SYNONYMS) {
			specialKeywordsDictionary.put(entry, CommandType.MARK_ALL);
			relativeTimeKeywordsDictionary.put(entry, RelativeType.ALL);
		}
		for (String entry : LIST_RELATIVE_TIME_KEYWORDS_BEFORE) {
			relativeTimeKeywordsDictionary.put(entry, RelativeType.BEFORE);
		}
		for (String entry : LIST_RELATIVE_TIME_KEYWORDS_AFTER) {
			relativeTimeKeywordsDictionary.put(entry, RelativeType.AFTER);
		}
	}

	public Command parseCommand(String inputCommand)
			throws CommandCouldNotBeParsedException,
			StartTimeAfterEndTimeException {
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
		Pattern timePattern = Pattern.compile(PATTERN_TIME_DOT_SEPARATOR);
		for (String word : words) {
			if (word.matches(PATTERN_DATE)) {
				String newDate = convertToMiddleEndian(word.replaceAll(DASH,
						SLASH));
				stringToProcess = stringToProcess.replace(word, newDate);
			}
			Matcher patternMatcher = timePattern.matcher(word);
			if (patternMatcher.find()) {
				String matchingGroup = patternMatcher.group(FIRST_GROUP);
				String newTime = matchingGroup.replace(DOT, COLON);
				String newWord = word.replace(
						patternMatcher.group(FIRST_GROUP), newTime);
				stringToProcess = stringToProcess.replace(word, newWord);
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

	private String removeWronglyParsedDates(String dateString) {
		String[] components = dateString.split(WHITE_SPACE);
		for (String component : components) {
			String componentLowerCase = component.toLowerCase();
			for (String dateVariant : LIST_SHORT_DATE_VARIANTS) {
				if (componentLowerCase.contains(dateVariant)) {
					if (!componentLowerCase.equals(dateVariant)) {
						dateString = dateString
								.replace(component, EMPTY_STRING);
					}
					break;
				}
			}
			if ((disallowedStartCharsDictionary.contains(componentLowerCase
					.charAt(START_INDEX)) && !dateVariantsDictionary
					.contains(componentLowerCase))
					|| (component.matches(PATTERN_ALPHANUMERIC_WORD)
							&& !component.matches(PATTERN_NON_DIGIT)
							&& !component.matches(PATTERN_NUMBER) && !componentLowerCase
								.matches(PATTERN_TIME))) {
				dateString = dateString.replace(component, EMPTY_STRING);
			}
		}
		return dateString;
	}

	// TODO get rid of the error of parsing substring "wedding", "fries" etc.
	private DateTime[] getStartAndEndTimesFromCommand() {
		Parser dateParser = new Parser();
		String stringToParse = removeWronglyParsedDates(commandToParse);
		List<DateGroup> dateGroupList = dateParser.parse(stringToParse);
		DateTime[] startAndEndTime = new DateTime[NUM_START_AND_END_TIMES];
		if (dateGroupList.isEmpty()) {
			return startAndEndTime;
		}
		while (!dateGroupList.isEmpty()) {
			DateGroup dateGroup = dateGroupList.get(FIRST_GROUP);
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
					stringToParse = stringToParse.replace(dateGroup.getText(),
							EMPTY_STRING);
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
					stringToParse = stringToParse.replace(dateGroup.getText(),
							EMPTY_STRING);
					commandToParse = commandToParse.replace(
							dateGroup.getText(), EMPTY_STRING);
					return startAndEndTime;
				}
				// reparse to see if there is an alternative start time
				stringToParse = stringToParse.replace(dateGroup.getText(),
						EMPTY_STRING);
				commandToParse = commandToParse.replace(dateGroup.getText(),
						EMPTY_STRING);
				dateGroupList = dateParser.parse(stringToParse);
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
				stringToParse = stringToParse.replace(dateGroup.getText(),
						EMPTY_STRING);
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
				startAndEndTime[END_TIME] = new DateTime(dateGroup.getDates()
						.get(FIRST_GROUP));
			}
			stringToParse = stringToParse.replace(dateGroup.getText(),
					EMPTY_STRING);
			commandToParse = commandToParse.replace(dateGroup.getText(),
					EMPTY_STRING);
			dateGroupList = dateParser.parse(stringToParse);
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

	private CommandAdd parseAdd() throws CommandCouldNotBeParsedException,
			StartTimeAfterEndTimeException {
		logger.log(Level.INFO, "Parsing as add command.");
		DateTime[] startAndEndTime = getStartAndEndTimesFromCommand();
		if (compareNullDatesLast(startAndEndTime[START_TIME],
				startAndEndTime[END_TIME]) > SAME_TIME) {
			throw new StartTimeAfterEndTimeException();
		}
		String taskName = removeExtraWhiteSpaces(commandToParse);
		if (taskName.length() == 0) {
			throw new CommandCouldNotBeParsedException();
		}
		return new CommandAdd(new Task(taskName, startAndEndTime[START_TIME],
				startAndEndTime[END_TIME]));
	}

	private CommandEdit parseEdit() throws CommandCouldNotBeParsedException,
			StartTimeAfterEndTimeException {
		logger.log(Level.INFO, "Parsing as edit command.");
		String taskName = null;
		int taskIndex;
		Pattern anyNumberPattern = Pattern.compile(PATTERN_ANY_NUMBER_NOT_DATE);
		Matcher patternMatcher = anyNumberPattern.matcher(commandToParse);
		if (patternMatcher.find()) {
			taskIndex = Integer.parseInt(patternMatcher.group(FIRST_GROUP)
					.trim());
			System.out.println(taskIndex);
			if (taskIndex < FIRST_VALID_INDEX) {
				throw new CommandCouldNotBeParsedException();
			}
			commandToParse = commandToParse.replaceAll(
					PATTERN_ANY_NUMBER_NOT_DATE, EMPTY_STRING);
		} else {
			throw new CommandCouldNotBeParsedException();
		}
		DateTime[] startAndEndTime = getStartAndEndTimesFromCommand();
		if (compareNullDatesLast(startAndEndTime[START_TIME],
				startAndEndTime[END_TIME]) > SAME_TIME) {
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
		Pattern anyNumberPattern = Pattern.compile(PATTERN_NON_DIGIT);
		Matcher patternMatcher = anyNumberPattern.matcher(command);
		if (patternMatcher.find()) {
			return new CommandMark(Integer.parseInt(patternMatcher
					.group(FIRST_GROUP)));
		}
		throw new CommandCouldNotBeParsedException();
	}

	private CommandSearch parseSearch()
			throws CommandCouldNotBeParsedException,
			StartTimeAfterEndTimeException {
		logger.log(Level.INFO, "Parsing as search command.");
		DateTime[] startAndEndTime = getStartAndEndTimesFromCommand();
		if (compareNullDatesLast(startAndEndTime[START_TIME],
				startAndEndTime[END_TIME]) > SAME_TIME) {
			throw new StartTimeAfterEndTimeException();
		}
		commandToParse = removeExtraWhiteSpaces(commandToParse);
		if (commandToParse.length() == 0) {
			commandToParse = null;
		}
		RelativeType relativeType = getRelativeKeyword();
		System.out.println("a" + IMPOSSIBLY_LARGE_DATE);
		System.out.println("b" + IMPOSSIBLY_SMALL_DATE);
		System.out.println(new DateTime());
		if (startAndEndTime[START_TIME] != null) {
			switch (relativeType) {
			case BEFORE:
				return new CommandSearch(commandToParse, IMPOSSIBLY_SMALL_DATE,
						startAndEndTime[START_TIME]);
			case AFTER:
				return new CommandSearch(commandToParse,
						startAndEndTime[START_TIME], IMPOSSIBLY_LARGE_DATE);
			}
		}
		if (relativeType == RelativeType.ALL) {
			return new CommandSearch(EMPTY_STRING, null, null);
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

	private RelativeType getRelativeKeyword() {
		String[] wordsInCommand = commandToParse.split(WHITE_SPACE);
		for (String word : wordsInCommand) {
			if (relativeTimeKeywordsDictionary.containsKey(word.toLowerCase())) {
				commandToParse = commandToParse.replace(word, EMPTY_STRING);
				commandToParse = removeExtraWhiteSpaces(commandToParse);
				return relativeTimeKeywordsDictionary.get(word.toLowerCase());
			}
		}
		return RelativeType.NOT;
	}

	private CommandType getCommandType()
			throws CommandCouldNotBeParsedException {
		logger.log(Level.INFO, "Getting command type.");
		String[] wordsInCommand = commandToParse.split(WHITE_SPACE);
		CommandType commandType = null;
		int indexOfWord = START_INDEX;
		for (String word : wordsInCommand) {
			if (commandTypeKeywordsDictionary.containsKey(word.toLowerCase())) {
				commandToParse = commandToParse.replace(word, EMPTY_STRING);
				commandToParse = removeExtraWhiteSpaces(commandToParse);
				commandType = commandTypeKeywordsDictionary.get(word
						.toLowerCase());
				break;
			}
			indexOfWord++;
		}
		int indexOfNextWord = indexOfWord + NEXT_WORD;
		if (commandType == CommandType.MARK
				&& indexOfNextWord < wordsInCommand.length) {
			if (specialKeywordsDictionary.get(wordsInCommand[indexOfNextWord]) == CommandType.MARK_ALL) {
				return CommandType.MARK_ALL;
			}
		}
		if (commandType == null) {
			throw new CommandCouldNotBeParsedException();
		}
		return commandType;
	}
}

enum RelativeType {
	BEFORE, AFTER, NOT, ALL;
}
