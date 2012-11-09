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
	private static final int YEAR_COMPONENT = 2;
	private static final int DAY_COMPONENT = 1;
	private static final int MONTH_COMPONENT = 0;
	private static final int SECOND_GROUP = 1;
	private static final int FIRST_VALID_INDEX = 1;
	private static final int FIRST_GROUP = 0;
	private static final int START_INDEX = 0;
	private static final int NEXT_WORD = 1;
	private static final int END_TIME = 1;
	private static final int START_TIME = 0;
	private static final int NUM_START_AND_END_TIMES = 2;
	private static final int POSITIVE_NUMBER = 1;
	private static final int SAME_TIME = 0;
	private static final int NEGATIVE_NUMBER = -1;
	private static final int MAX_ITERATIONS = 20;
	private static final int MILLISEC_DIFF_ALLOWANCE = 120;
	private static final String COLON = ":";
	private static final String DOT = ".";
	private static final String KEYWORD_FOR = "for";
	private static final String KEYWORD_DASH = " - ";
	private static final String KEYWORD_TO = " to ";
	private static final String KEYWORD_NOW = "now";
	private static final String SLASH = "/";
	private static final String DASH_OR_DOT = "[-|.]";
	private static final String EMPTY_STRING = "";
	private static final String MULTIPLE_WHITE_SPACES = "\\s+";
	private static final String WHITE_SPACE = " ";

	private static final DateTime IMPOSSIBLY_LARGE_DATE = new DateTime(
			Long.MAX_VALUE);
	private static final DateTime IMPOSSIBLY_SMALL_DATE = new DateTime(
			Long.MIN_VALUE);

	private static final char[] LIST_DISALLOWED_START_CHARS = { 'a', 'e', 'p',
			's', 'd', 'f', 'h', 'l', 'z', 'm', 'g', 't' };
	private static final String[] LIST_CONFLICTING_DATE_VARIANTS = { "mon",
			"tues", "wed", "thurs", "fri", "sat", "sun", "jan", "feb", "mar",
			"apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec" };
	private static final String[] LIST_OTHER_DATE_VARIANTS = { "monday",
			"tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",
			"january", "february", "march", "april", "may", "june", "july",
			"august", "september", "october", "november", "december", "for",
			"to", "now", "am", "pm", "day", "days", "hour", "hours", "hr",
			"hrs", "minute", "min", "minutes", "sec", "second", "seconds",
			"today", "tomorrow" };
	private static final String[] LIST_ADD_SYNONYMS = { "add", "insert",
			"create", "new", "put", "ins" };
	private static final String[] LIST_MARK_SYNONYMS = { "mark", "delete",
			"del", "remove", "discard", "erase", "drop", "clear", "rm" };
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

	private static final String PATTERN_ITH_NUMBER = "1st|\\d+1st|2nd|\\d+2nd|3rd|\\d+3rd|(\\d+th)";
	private static final String PATTERN_TIME = "(\\d{1,4}|\\d{1,2}[:|.]\\d{2})(am|pm)";
	private static final String PATTERN_ALPHANUMERIC_WORD = "\\w*";
	private static final String PATTERN_TIME_DOT_SEPARATOR = "\\d{1,2}[.]\\d{2}";
	private static final String PATTERN_DATE = "(\\d{1,2}[-|/|.]\\d{1,2}[-|/|.]\\d{2,4})|\\d{1,2}[-|/]\\d{1,2}";
	private static final String PATTERN_NON_DIGIT = "\\D+";
	private static final String PATTERN_NUMBER = "\\d+";
	private static final String PATTERN_ANY_NUMBER_NOT_DATE = "(^| )\\d+($| )";

	private static Logger logger = Logger.getLogger("JIMI");

	private HashMap<String, CommandType> commandTypeKeywordsDict;
	private HashMap<String, CommandType> specialKeywordsDict;
	private HashMap<String, RelativeType> relativeTimeKeywordsDict;
	private HashSet<String> otherDateVariantsDict;
	private HashSet<String> conflictingVariantsDict;
	private HashSet<Character> disallowedStartCharsDict;

	private String commandToParse;

	/**
	 * Initializes newly created CommandParser object.
	 */
	public CommandParser() {
		initialiseDictionaries();
	}

	/*
	 * Explanation for dictionaries: We need these dictionaries to look for
	 * keywords. The date parser (Natty) we are using has many constraints. Some
	 * dictionaries are used to facilitate extension of Natty.
	 */
	private void initialiseDictionaries() {
		commandTypeKeywordsDict = new HashMap<String, CommandType>();
		specialKeywordsDict = new HashMap<String, CommandType>();
		relativeTimeKeywordsDict = new HashMap<String, RelativeType>();
		otherDateVariantsDict = new HashSet<String>();
		conflictingVariantsDict = new HashSet<String>();
		disallowedStartCharsDict = new HashSet<Character>();
		for (String entry : LIST_CONFLICTING_DATE_VARIANTS) {
			conflictingVariantsDict.add(entry);
		}
		for (String entry : LIST_OTHER_DATE_VARIANTS) {
			otherDateVariantsDict.add(entry);
		}
		for (char entry : LIST_DISALLOWED_START_CHARS) {
			disallowedStartCharsDict.add(entry);
		}
		for (String entry : LIST_ADD_SYNONYMS) {
			commandTypeKeywordsDict.put(entry, CommandType.ADD);
		}
		for (String entry : LIST_EDIT_SYNONYMS) {
			commandTypeKeywordsDict.put(entry, CommandType.EDIT);
		}
		for (String entry : LIST_MARK_SYNONYMS) {
			commandTypeKeywordsDict.put(entry, CommandType.MARK);
		}
		for (String entry : LIST_SEARCH_SYNONYMS) {
			commandTypeKeywordsDict.put(entry, CommandType.SEARCH);
		}
		for (String entry : LIST_UNDO_SYNONYMS) {
			commandTypeKeywordsDict.put(entry, CommandType.UNDO);
		}
		for (String entry : LIST_REDO_SYNONYMS) {
			commandTypeKeywordsDict.put(entry, CommandType.REDO);
		}
		for (String entry : LIST_ALL_SYNONYMS) {
			specialKeywordsDict.put(entry, CommandType.MARK_ALL);
			relativeTimeKeywordsDict.put(entry, RelativeType.ALL);
		}
		for (String entry : LIST_RELATIVE_TIME_KEYWORDS_BEFORE) {
			relativeTimeKeywordsDict.put(entry, RelativeType.BEFORE);
		}
		for (String entry : LIST_RELATIVE_TIME_KEYWORDS_AFTER) {
			relativeTimeKeywordsDict.put(entry, RelativeType.AFTER);
		}
	}

	/**
	 * Parses input command and changes it to a appropriate Command object.
	 * 
	 * @param inputCommand
	 * @return Command
	 * @throws CommandCouldNotBeParsedException
	 * @throws StartTimeAfterEndTimeException
	 */
	public Command parseCommand(String inputCommand)
			throws CommandCouldNotBeParsedException,
			StartTimeAfterEndTimeException {
		assert (inputCommand != null) : "Null String.";
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
				parsedCommand = parseMark();
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
		assert (stringToProcess != null) : "Null String.";
		return stringToProcess.trim().replaceAll(MULTIPLE_WHITE_SPACES,
				WHITE_SPACE);
	}

	// Changes all dates and times to a standard format because date parser
	// cannot parse some formats properly.
	private String changeAllDatesToSameFormat(String stringToProcess) {
		assert (stringToProcess != null) : "Null String.";
		String[] words = stringToProcess.split(WHITE_SPACE);
		Pattern timePattern = Pattern.compile(PATTERN_TIME_DOT_SEPARATOR);
		for (String word : words) {
			if (word.matches(PATTERN_DATE)) {
				String newDate = convertToMiddleEndian(word.replaceAll(
						DASH_OR_DOT, SLASH));
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

	// Need this method because date parser only accepts dates in middle-endian
	// format.
	private String convertToMiddleEndian(String stringToProcess) {
		assert (stringToProcess != null) : "Null String.";
		String[] dateComponents = stringToProcess.split(SLASH);
		String newDate = dateComponents[DAY_COMPONENT] + SLASH
				+ dateComponents[MONTH_COMPONENT];
		if (dateComponents.length > YEAR_COMPONENT) {
			newDate += SLASH + dateComponents[YEAR_COMPONENT];
		}
		return newDate;
	}

	// Removes words that will be wrongly parsed by date parser (e.g. fries,
	// CS2103, anything starting with characters defined in
	// disallowedStartCharsDict)
	private String removeWronglyParsedWords(String dateString) {
		assert (dateString != null) : "Null String.";
		if (dateString.isEmpty()) {
			return dateString;
		}
		String[] components = dateString.split(WHITE_SPACE);
		for (int currIndex = START_INDEX; currIndex < components.length; currIndex++) {
			String componentLowerCase = components[currIndex].toLowerCase();
			for (String dateVariant : LIST_CONFLICTING_DATE_VARIANTS) {
				if (componentLowerCase.contains(dateVariant)) {
					if (!componentLowerCase.equals(dateVariant)
							&& !otherDateVariantsDict
									.contains(componentLowerCase)) {
						components[currIndex] = EMPTY_STRING;
					}
					break;
				}

			}
			if (isUnparsable(componentLowerCase)) {
				components[currIndex] = EMPTY_STRING;
			}
		}
		return rebuildString(components);
	}

	private boolean isUnparsable(String word) {
		assert (word != null) : "Null String.";
		return (disallowedStartCharsDict.contains(word.charAt(START_INDEX))
				&& !otherDateVariantsDict.contains(word) && !conflictingVariantsDict
					.contains(word))
				|| (word.matches(PATTERN_ALPHANUMERIC_WORD)
						&& !word.matches(PATTERN_NON_DIGIT)
						&& !word.matches(PATTERN_NUMBER)
						&& !word.matches(PATTERN_TIME) && !word
							.matches(PATTERN_ITH_NUMBER));
	}

	private String rebuildString(String[] stringArray) {
		assert (stringArray != null) : "Null String Array.";
		String rebuiltString = EMPTY_STRING;
		for (String element : stringArray) {
			rebuiltString += WHITE_SPACE + element;
		}
		return rebuiltString;
	}

	private DateTime[] getStartAndEndTimesFromCommand() {
		Parser dateParser = new Parser();
		String stringToParse = removeWronglyParsedWords(commandToParse);
		stringToParse = removeExtraWhiteSpaces(stringToParse);
		List<DateGroup> dateGroupList = dateParser.parse(stringToParse);
		DateTime[] startAndEndTime = new DateTime[NUM_START_AND_END_TIMES];
		if (dateGroupList.isEmpty()) {
			return startAndEndTime;
		}
		int numIterations = START_INDEX; // in case of possible infinite loop
		while (!dateGroupList.isEmpty() && numIterations < MAX_ITERATIONS) {
			DateGroup dateGroup = dateGroupList.get(FIRST_GROUP);
			logger.log(Level.INFO, "Parsing Date: " + dateGroup.getText());

			if ((dateGroup.getText().contains(KEYWORD_TO) | dateGroup.getText()
					.contains(KEYWORD_DASH)) && dateGroup.getDates().size() > 1) {
				startAndEndTime[START_TIME] = new DateTime(dateGroup.getDates()
						.get(FIRST_GROUP));
				startAndEndTime[END_TIME] = new DateTime(dateGroup.getDates()
						.get(SECOND_GROUP));
				if (!dateGroup.getText().contains(KEYWORD_NOW)) {
					if (!isTimeSpecified(startAndEndTime[START_TIME])) {
						startAndEndTime[START_TIME] = startAndEndTime[START_TIME]
								.withTimeAtStartOfDay();
					}
					if (!isTimeSpecified(startAndEndTime[END_TIME])) {
						startAndEndTime[END_TIME] = startAndEndTime[END_TIME]
								.withTimeAtStartOfDay();

					}
				} else {
					if (!isNow(startAndEndTime[START_TIME])
							&& !isTimeSpecified(startAndEndTime[START_TIME])) {
						startAndEndTime[START_TIME] = startAndEndTime[START_TIME]
								.withTimeAtStartOfDay();
					}
					if (!isNow(startAndEndTime[END_TIME])
							&& !isTimeSpecified(startAndEndTime[END_TIME])) {
						startAndEndTime[END_TIME] = startAndEndTime[END_TIME]
								.withTimeAtStartOfDay();
					}
				}
				stringToParse = stringToParse.replace(dateGroup.getText(),
						EMPTY_STRING);
				commandToParse = commandToParse.replace(dateGroup.getText(),
						EMPTY_STRING);
				return startAndEndTime;
			}

			if (dateGroup.getText().contains(KEYWORD_FOR)
					&& !dateGroup.getDates().isEmpty()) {
				DateTime intervalStart = new DateTime(dateGroup.getDates().get(
						FIRST_GROUP));
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
				if (!dateGroupList.isEmpty()) {
					dateGroup = dateGroupList.get(FIRST_GROUP);
					startAndEndTime[START_TIME] = new DateTime(dateGroup
							.getDates().get(FIRST_GROUP));
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
						.get(FIRST_GROUP));
				if (!dateGroup.getText().contains(KEYWORD_NOW)
						&& !isTimeSpecified(startAndEndTime[START_TIME])) {
					startAndEndTime[START_TIME] = startAndEndTime[START_TIME]
							.withTimeAtStartOfDay();
				}
			} else if (startAndEndTime[END_TIME] == null) {
				startAndEndTime[END_TIME] = new DateTime(dateGroup.getDates()
						.get(FIRST_GROUP));
			}

			stringToParse = stringToParse.replace(dateGroup.getText(),
					EMPTY_STRING);
			commandToParse = commandToParse.replace(dateGroup.getText(),
					EMPTY_STRING);
			dateGroupList = dateParser.parse(stringToParse);
			numIterations++;
		}
		return startAndEndTime;
	}

	private boolean isNow(DateTime dateTimeToCheck) {
		assert(dateTimeToCheck != null) : "Null DateTime.";
		long timeNow = new DateTime().getMillis();
		long timeSpecified = dateTimeToCheck.getMillis();
		if (Math.abs(timeNow - timeSpecified) <= MILLISEC_DIFF_ALLOWANCE) {
			return true;
		}
		return false;
	}

	private boolean isTimeSpecified(DateTime dateTimeToCheck) {
		assert(dateTimeToCheck != null) : "Null DateTime.";
		int timeNow = new DateTime().getMillisOfDay();
		int timeSpecified = dateTimeToCheck.getMillisOfDay();
		if (Math.abs(timeNow - timeSpecified) <= MILLISEC_DIFF_ALLOWANCE) {
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
		if (taskName.isEmpty()) {
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
			commandToParse = commandToParse.replaceFirst(
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

	private CommandMark parseMark() throws CommandCouldNotBeParsedException {
		logger.log(Level.INFO, "Parsing as mark command.");
		Pattern anyNumberPattern = Pattern.compile(PATTERN_NUMBER);
		Matcher patternMatcher = anyNumberPattern.matcher(commandToParse);
		if (patternMatcher.find()) {
			int taskIndex = Integer.parseInt(patternMatcher.group(FIRST_GROUP));
			if (taskIndex < FIRST_VALID_INDEX) {
				throw new CommandCouldNotBeParsedException();
			}
			return new CommandMark(taskIndex);
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
		RelativeType relativeType = getRelativeKeyword();
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
		if (commandToParse.isEmpty() && startAndEndTime[START_TIME] == null
				&& startAndEndTime[END_TIME] == null) {
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

	private RelativeType getRelativeKeyword() {
		String[] wordsInCommand = commandToParse.split(WHITE_SPACE);
		for (String word : wordsInCommand) {
			if (relativeTimeKeywordsDict.containsKey(word.toLowerCase())) {
				commandToParse = commandToParse.replace(word, EMPTY_STRING);
				commandToParse = removeExtraWhiteSpaces(commandToParse);
				return relativeTimeKeywordsDict.get(word.toLowerCase());
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
			if (commandTypeKeywordsDict.containsKey(word.toLowerCase())) {
				commandToParse = commandToParse
						.replaceFirst(word, EMPTY_STRING);
				commandToParse = removeExtraWhiteSpaces(commandToParse);
				commandType = commandTypeKeywordsDict.get(word.toLowerCase());
				break;
			}
			indexOfWord++;
		}
		int indexOfNextWord = indexOfWord + NEXT_WORD;
		if (commandType == CommandType.MARK
				&& indexOfNextWord < wordsInCommand.length) {
			if (specialKeywordsDict.get(wordsInCommand[indexOfNextWord]) == CommandType.MARK_ALL) {
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
