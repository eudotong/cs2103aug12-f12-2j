package commandLogic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

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
import utilities.DateComparator;
import utilities.Task;
import exceptions.CommandCouldNotBeParsedException;
import exceptions.IncorrectDateFormatException;
import exceptions.StartTimeAfterEndTimeException;

/**
 * The CommandParser takes in a command as a String and returns a Command object
 * accordingly
 * 
 * @author A0088278L
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
	private static final int SAME_TIME = 0;
	private static final int NEGATIVE_NUMBER = -1;
	private static final int MAX_ITERATIONS = 20;
	private static final String COLON = ":";
	private static final String DOT = ".";
	private static final String KEYWORD_FOR = "for ";
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
	private static final DateTimeParser[] DATE_PARSERS = {
			DateTimeFormat.forPattern("MM/dd/yyyy").getParser(),
			DateTimeFormat.forPattern("MM/d/yyyy").getParser(),
			DateTimeFormat.forPattern("M/d/yy").getParser(),
			DateTimeFormat.forPattern("MM/dd/yy").getParser(),
			DateTimeFormat.forPattern("M/d").getParser(),
			DateTimeFormat.forPattern("MM/dd").getParser() };
	private static final DateTimeFormatter DATE_TIME_FORMATTER = new DateTimeFormatterBuilder()
			.append(null, DATE_PARSERS).toFormatter();
	
	private static final String[] LIST_CONFLICTING_DATE_VARIANTS = { "mon",
			"tues", "wed", "thurs", "fri", "sat", "sun", "jan", "feb", "mar",
			"apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec" };
	private static final String[] LIST_ALL_DATE_VARIANTS = { "mon", "tues",
			"wed", "thurs", "fri", "sat", "sun", "jan", "feb", "mar", "apr",
			"may", "jun", "jul", "aug", "sep", "oct", "nov", "dec", "monday",
			"tuesday", "wednesday", "thursday", "friday", "saturday", "sunday",
			"january", "february", "march", "april", "may", "june", "july",
			"august", "september", "october", "november", "december", "for",
			"to", "now", "am", "pm", "day", "days", "hour", "hours", "hr",
			"hrs", "minute", "min", "minutes", "sec", "second", "seconds",
			"today", "tomorrow", "yesterday", "next", "month", "week",
			"months", "weeks", "from", "last", "year", "years" };
	private static final String[] LIST_ADD_SYNONYMS = { "add", "insert",
			"create", "new", "put", "ins" };
	private static final String[] LIST_MARK_SYNONYMS = { "mark", "delete",
			"del", "remove", "discard", "erase", "drop", "clear", "rm" };
	private static final String[] LIST_EDIT_SYNONYMS = { "update", "edit",
			"change", "alter", "modify" };
	private static final String[] LIST_SEARCH_SYNONYMS = { "find", "display",
			"search", "show", "view", "lookup" };
	private static final String[] LIST_ALL_SYNONYMS = { "all", "everything" };
	private static final String[] LIST_UPCOMING_SYNONYMS = { "upcoming" };
	private static final String[] LIST_REDO_SYNONYMS = { "redo" };
	private static final String[] LIST_UNDO_SYNONYMS = { "undo" };
	private static final String[] LIST_RELATIVE_TIME_KEYWORDS_BEFORE = {
			"before", "b4", "bef" };
	private static final String[] LIST_RELATIVE_TIME_KEYWORDS_AFTER = {
			"after", "aft", "onwards", "later" };

	private static final String PATTERN_ITH_NUMBER = "1st|\\d+1st|2nd|\\d+2nd|3rd|\\d+3rd|(\\d+th)";
	private static final String PATTERN_TIME = "(\\d{1,4}|\\d{1,2}[:|.]\\d{2})(am|pm)";
	private static final String PATTERN_ALPHANUMERIC_WORD = "\\w*";
	private static final String PATTERN_TIME_DOT_SEPARATOR = "\\d{1,2}[.]\\d{2}";
	private static final String PATTERN_DATE = "(\\d+[-|/|.]\\d+[-|/|.]\\d+)|\\d+[-|/]\\d+";
	private static final String PATTERN_ALPHABET_ONLY_WORD = "[a-z]*";
	private static final String PATTERN_NON_DIGIT = "\\D+";
	private static final String PATTERN_NUMBER = "\\d+";
	private static final String PATTERN_ANY_NUMBER_NOT_DATE = "(^| )\\d+($| )";
	private static final String PATTERN_EXACT_MATCH = "(^| )%s($| )";

	private static Logger logger = Logger.getLogger("JIMI");

	private HashMap<String, CommandType> commandTypeKeywordsDict;
	private HashMap<String, CommandType> specialKeywordsDict;
	private HashMap<String, RelativeType> relativeTimeKeywordsDict;
	private HashSet<String> allDateVariantsDict;

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
		allDateVariantsDict = new HashSet<String>();
		for (String entry : LIST_ALL_DATE_VARIANTS) {
			allDateVariantsDict.add(entry);
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
		for (String entry : LIST_UPCOMING_SYNONYMS) {
			relativeTimeKeywordsDict.put(entry, RelativeType.UPCOMING);
		}
	}

	/**
	 * Parses input command and changes it to a appropriate Command object.
	 * 
	 * @param inputCommand
	 * @return Command
	 * @throws CommandCouldNotBeParsedException
	 * @throws StartTimeAfterEndTimeException
	 * @throws IncorrectDateFormatException
	 */
	public Command parseCommand(String inputCommand)
			throws CommandCouldNotBeParsedException,
			StartTimeAfterEndTimeException, IncorrectDateFormatException {
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
	private String changeAllDatesToSameFormat(String stringToProcess)
			throws IncorrectDateFormatException {
		assert (stringToProcess != null) : "Null String.";
		String[] words = stringToProcess.split(WHITE_SPACE);
		Pattern timePattern = Pattern.compile(PATTERN_TIME_DOT_SEPARATOR);
		for (String word : words) {
			stringToProcess = changeToStandardDateFormat(stringToProcess, word);
			stringToProcess = changeToStandardTimeFormat(stringToProcess,
					timePattern, word);
		}
		return stringToProcess;
	}

	private String changeToStandardTimeFormat(String stringToProcess,
			Pattern timePattern, String word) {
		Matcher patternMatcher = timePattern.matcher(word);
		if (patternMatcher.find()) {
			String matchingGroup = patternMatcher.group(FIRST_GROUP);
			String newTime = matchingGroup.replace(DOT, COLON);
			String newWord = word.replace(patternMatcher.group(FIRST_GROUP),
					newTime);
			stringToProcess = stringToProcess.replace(word, newWord);
		}
		return stringToProcess;
	}

	private String changeToStandardDateFormat(String stringToProcess,
			String word) throws IncorrectDateFormatException {
		if (word.matches(PATTERN_DATE)) {
			String newDate = convertToMiddleEndian(word.replaceAll(DASH_OR_DOT,
					SLASH));
			stringToProcess = stringToProcess.replace(word, newDate);
		}
		return stringToProcess;
	}

	// Need this method because date parser only accepts dates in middle-endian
	// format (i.e. mm-dd-yyyy)
	private String convertToMiddleEndian(String stringToProcess)
			throws IncorrectDateFormatException {
		assert (stringToProcess != null) : "Null String.";
		String[] dateComponents = stringToProcess.split(SLASH);
		String newDate = dateComponents[DAY_COMPONENT] + SLASH
				+ dateComponents[MONTH_COMPONENT];
		if (dateComponents.length > YEAR_COMPONENT) {
			newDate += SLASH + dateComponents[YEAR_COMPONENT];
		}
		if (isCorrectDate(newDate)) {
			return newDate;
		}
		throw new IncorrectDateFormatException();
	}

	private boolean isCorrectDate(String date) {
		try {
			DATE_TIME_FORMATTER.parseDateTime(date);
			return true;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	// Removes words that will be wrongly parsed by date parser (e.g. fries,
	// CS2103 and all words that do not look like date keywords)
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
					if (!allDateVariantsDict.contains(componentLowerCase)) {
						components[currIndex] = EMPTY_STRING;
					}
					break;
				}
			}
			if (isUnparsable(componentLowerCase)) {
				components[currIndex] = EMPTY_STRING;
			}
		}
		// Need to rebuild the string using the array like this because
		// replaceFirst() can sometimes wrongly replace words
		return rebuildString(components);
	}

	private boolean isUnparsable(String word) {
		assert (word != null) : "Null String.";
		return (word.matches(PATTERN_ALPHABET_ONLY_WORD) && !allDateVariantsDict
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

	private String removeWordsFromString(String wordsToRemove,
			String stringToProcess) {
		String newString = stringToProcess;
		String[] words = wordsToRemove.split(WHITE_SPACE);
		for (String word : words) {
			newString = newString.replaceFirst(
					String.format(PATTERN_EXACT_MATCH, word), WHITE_SPACE);
		}
		return removeExtraWhiteSpaces(newString);
	}

	private DateTime[] getAndRemoveStartAndEndTimesFromCommand() {
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
			if ((dateGroup.getText().contains(KEYWORD_TO) || dateGroup
					.getText().contains(KEYWORD_DASH))
					&& !dateGroup.getDates().isEmpty()) {
				startAndEndTime[START_TIME] = new DateTime(dateGroup.getDates()
						.get(FIRST_GROUP));
				startAndEndTime[END_TIME] = new DateTime(dateGroup.getDates()
						.get(SECOND_GROUP));
				// if the time is not specified, should set time to 12am
				boolean isStartTimeSpecified = DateComparator
						.isSameTimeOfDayAsNow(startAndEndTime[START_TIME]);
				boolean isEndTimeSpecified = DateComparator
						.isSameTimeOfDayAsNow(startAndEndTime[END_TIME]);
				if (!dateGroup.getText().contains(KEYWORD_NOW)) {
					if (!isStartTimeSpecified) {
						startAndEndTime[START_TIME] = startAndEndTime[START_TIME]
								.withTimeAtStartOfDay();
					}
					if (!isEndTimeSpecified) {
						startAndEndTime[END_TIME] = startAndEndTime[END_TIME]
								.withTimeAtStartOfDay();
					}
				} else {
					if (!DateComparator.isNow(startAndEndTime[START_TIME])
							&& !isStartTimeSpecified) {
						startAndEndTime[START_TIME] = startAndEndTime[START_TIME]
								.withTimeAtStartOfDay();
					}
					if (!DateComparator.isNow(startAndEndTime[END_TIME])
							&& !isEndTimeSpecified) {
						startAndEndTime[END_TIME] = startAndEndTime[END_TIME]
								.withTimeAtStartOfDay();
					}
				}
				stringToParse = removeWordsFromString(dateGroup.getText(),
						stringToParse);
				commandToParse = removeWordsFromString(dateGroup.getText(),
						commandToParse);
				return startAndEndTime;
			}

			if (dateGroup.getText().contains(KEYWORD_FOR)
					&& !dateGroup.getDates().isEmpty()) {
				DateTime intervalStart = new DateTime(dateGroup.getDates().get(
						FIRST_GROUP));
				DateTime intervalEnd = new DateTime(dateGroup.getDates().get(
						SECOND_GROUP));
				long intervalDifference = intervalEnd.getMillis()
						- intervalStart.getMillis();
				if (startAndEndTime[START_TIME] != null) {
					startAndEndTime[END_TIME] = startAndEndTime[START_TIME]
							.plus(intervalDifference);
					stringToParse = removeWordsFromString(dateGroup.getText(),
							stringToParse);
					commandToParse = removeWordsFromString(dateGroup.getText(),
							commandToParse);
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
				stringToParse = removeWordsFromString(dateGroup.getText(),
						stringToParse);
				commandToParse = removeWordsFromString(dateGroup.getText(),
						commandToParse);
				return startAndEndTime;
			}

			if (startAndEndTime[START_TIME] == null) {
				startAndEndTime[START_TIME] = new DateTime(dateGroup.getDates()
						.get(FIRST_GROUP));
				if (!dateGroup.getText().contains(KEYWORD_NOW)
						&& !DateComparator
								.isSameTimeOfDayAsNow(startAndEndTime[START_TIME])) {
					startAndEndTime[START_TIME] = startAndEndTime[START_TIME]
							.withTimeAtStartOfDay();
				}
			} else if (startAndEndTime[END_TIME] == null) {
				startAndEndTime[END_TIME] = new DateTime(dateGroup.getDates()
						.get(FIRST_GROUP));
				if (startAndEndTime[START_TIME]
						.isAfter(startAndEndTime[END_TIME])) {
					DateTime swapHelper = startAndEndTime[START_TIME];
					startAndEndTime[START_TIME] = startAndEndTime[END_TIME];
					startAndEndTime[END_TIME] = swapHelper;
				}
			}
			stringToParse = removeWordsFromString(dateGroup.getText(),
					stringToParse);
			commandToParse = removeWordsFromString(dateGroup.getText(),
					commandToParse);
			dateGroupList = dateParser.parse(stringToParse);
			numIterations++;
		}
		return startAndEndTime;
	}

	private CommandAdd parseAdd() throws CommandCouldNotBeParsedException,
			StartTimeAfterEndTimeException {
		logger.log(Level.INFO, "Parsing as add command.");
		DateTime[] startAndEndTime = getAndRemoveStartAndEndTimesFromCommand();
		if (DateComparator.compareNullDatesLast(startAndEndTime[START_TIME],
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
		int taskIndex = getAndRemoveTaskIndexFromCommand(PATTERN_ANY_NUMBER_NOT_DATE);
		if (taskIndex < FIRST_VALID_INDEX) {
			throw new CommandCouldNotBeParsedException();
		}
		DateTime[] startAndEndTime = getAndRemoveStartAndEndTimesFromCommand();
		if (DateComparator.compareNullDatesLast(startAndEndTime[START_TIME],
				startAndEndTime[END_TIME]) > SAME_TIME) {
			throw new StartTimeAfterEndTimeException();
		}
		taskName = removeExtraWhiteSpaces(commandToParse);
		Task newTask = new Task(taskName, startAndEndTime[START_TIME],
				startAndEndTime[END_TIME]);
		return new CommandEdit(taskIndex, newTask);
	}

	private int getAndRemoveTaskIndexFromCommand(String numberPattern) {
		Pattern anyNumberPattern = Pattern.compile(numberPattern);
		Matcher patternMatcher = anyNumberPattern.matcher(commandToParse);
		int taskIndex = NEGATIVE_NUMBER;
		if (patternMatcher.find()) {
			taskIndex = Integer.parseInt(patternMatcher.group(FIRST_GROUP)
					.trim());
		}
		commandToParse = commandToParse.replaceFirst(numberPattern,
				EMPTY_STRING);
		return taskIndex;
	}

	private CommandMark parseMark() throws CommandCouldNotBeParsedException {
		logger.log(Level.INFO, "Parsing as mark command.");
		int taskIndex = getAndRemoveTaskIndexFromCommand(PATTERN_NUMBER);
		if (taskIndex < FIRST_VALID_INDEX) {
			throw new CommandCouldNotBeParsedException();
		}
		return new CommandMark(taskIndex);
	}

	private CommandSearch parseSearch()
			throws CommandCouldNotBeParsedException,
			StartTimeAfterEndTimeException {
		logger.log(Level.INFO, "Parsing as search command.");
		DateTime[] startAndEndTime = getAndRemoveStartAndEndTimesFromCommand();
		if (DateComparator.compareNullDatesLast(startAndEndTime[START_TIME],
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
		if (relativeType == RelativeType.UPCOMING
				&& startAndEndTime[START_TIME] == null
				&& startAndEndTime[END_TIME] == null) {
			commandToParse = null;
		}
		return new CommandSearch(commandToParse, startAndEndTime[START_TIME],
				startAndEndTime[END_TIME]);
	}

	private RelativeType getRelativeKeyword() {
		String[] wordsInCommand = commandToParse.split(WHITE_SPACE);
		for (String word : wordsInCommand) {
			if (relativeTimeKeywordsDict.containsKey(word.toLowerCase())) {
				commandToParse = commandToParse
						.replaceFirst(word, EMPTY_STRING);
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
	BEFORE, AFTER, NOT, UPCOMING, ALL;
}
