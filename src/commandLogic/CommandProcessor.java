package commandLogic;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import storage.TaskRecords;
import utilities.Command;
import utilities.CommandSearch;
import utilities.Task;
import exceptions.CommandCouldNotBeParsedException;
import exceptions.NothingToRedoException;
import exceptions.NothingToUndoException;
import exceptions.StartTimeAfterEndTimeException;

public class CommandProcessor {
	private static final int TASK_INDEX_START = 0;
	private static final int SAME_TIME = 0;

	private static final String MESSAGE_ERROR_START_TIME_AFTER_END_TIME = "Error: Start date/time is after end date/time.";
	private static final String EMPTY_STRING = "";
	private static final String MESSAGE_ERROR_UNRECOGNISED_COMMAND = "Command not recognised.";
	private static final String MESSAGE_ERROR_UNABLE_TO_UNDO = "There are no commands to undo";
	private static final String MESSAGE_ERROR_UNABLE_TO_REDO = "There are no commands to redo";
	private static final DateTimeFormatter DATE_FORMATTER_DAY_DATE = DateTimeFormat
			.forPattern("E, d MMM");

	private static Logger logger = Logger.getLogger("JIMI");

	private ChangeRecord changeRecord;
	private TaskRecords taskRecords;
	private CommandParser commandParser;
	private Command latestSearch;

	public CommandProcessor() throws IOException {
		FileHandler fileHandler = new FileHandler("log/app.log", true);
		logger.addHandler(fileHandler);
		changeRecord = new ChangeRecord();
		commandParser = new CommandParser();
		taskRecords = TaskRecords.getInstance();
		latestSearch = new CommandSearch(EMPTY_STRING, null, null);
		latestSearch.processCommand(taskRecords);
	}

	public String processCommand(String command) {
		try {
			logger.log(Level.INFO, "The command: \"" + command
					+ "\" was issued.");
			changeRecord.add(command);
			String outputMessage = EMPTY_STRING;
			Command commandIssued = commandParser.parseCommand(command);
			switch (commandIssued.getCommandType()) {
			case ADD:
				logger.log(Level.INFO, "Processing add.");
				outputMessage = processAdd(commandIssued);
				break;
			case EDIT:
				logger.log(Level.INFO, "Processing edit.");
				outputMessage = processEdit(commandIssued);
				break;
			case MARK:
				logger.log(Level.INFO, "Processing mark.");
				outputMessage = processMark(commandIssued);
				break;
			case SEARCH:
				logger.log(Level.INFO, "Processing search.");
				outputMessage = processSearch(commandIssued);
				break;
			case UNDO:
				logger.log(Level.INFO, "Processing undo.");
				outputMessage = processUndo();
				break;
			case REDO:
				logger.log(Level.INFO, "Processing redo.");
				outputMessage = processRedo();
				break;
			case MARK_ALL:
				logger.log(Level.INFO, "Processing mark all.");
				outputMessage = processMarkAll(commandIssued);
			}
			changeRecord.add(commandIssued);
			return outputMessage;
		} catch (CommandCouldNotBeParsedException e) {
			logger.log(Level.WARNING, "Error: command not recognised");
			return MESSAGE_ERROR_UNRECOGNISED_COMMAND;
		} catch (StartTimeAfterEndTimeException e) {
			logger.log(Level.WARNING, "Error: start time is after end time.");
			return MESSAGE_ERROR_START_TIME_AFTER_END_TIME;
		}
	}

	private String processMarkAll(Command command) {
		return command.processCommand(taskRecords);
	}

	private String processSearch(Command command) {
		latestSearch = command;
		return command.processCommand(taskRecords);
	}

	private String processAdd(Command command) {
		return command.processCommand(taskRecords);
	}

	private String processEdit(Command command) {
		return command.processCommand(taskRecords);
	}

	private String processMark(Command command) {
		return command.processCommand(taskRecords);
	}

	private String processUndo() {
		try {
			Command commandToUndo = changeRecord.undo();
			return commandToUndo.processCommand(taskRecords);
		} catch (NothingToUndoException e) {
			logger.log(Level.WARNING, "Error: nothing to undo");
			return MESSAGE_ERROR_UNABLE_TO_UNDO;
		}
	}

	private String processRedo() {
		try {
			Command commandToRedo = changeRecord.redo();
			return commandToRedo.processCommand(taskRecords);
		} catch (NothingToRedoException e) {
			logger.log(Level.WARNING, "Error: nothing to redo");
			return MESSAGE_ERROR_UNABLE_TO_REDO;
		}
	}

	// Note that we keep the super long magic strings here because if we
	// extract, String.format() returns:
	// java.util.UnknownFormatConversionException
	public DefaultListModel<String> getCurrentListModelOfTasks() {
		latestSearch.processCommand(taskRecords);
		Task[] currentListOfTasks = taskRecords.getCurrentListOfTasks();
		DateTime currentDateIteration = null;
		DefaultListModel<String> currentListOfTasksModel = new DefaultListModel<String>();
		for (int indexOfTask = TASK_INDEX_START; indexOfTask < currentListOfTasks.length; indexOfTask++) {
			if (!isSameDay(currentDateIteration,
					currentListOfTasks[indexOfTask].getStartTime())) {
				currentDateIteration = currentListOfTasks[indexOfTask]
						.getStartTime();
				String element = "<html><head><style>	p.padding {padding-left:0.3cm;} <style/><head/>"
						+ "<body style=\"width:290px\"><hr align=\"right\" width=\"98%\">"
						+ "<font size=\"5\" face=\"Georgia, Arial\" color=\"maroon\"><p class=\"padding\">"
						+ currentDateIteration
								.toString(DATE_FORMATTER_DAY_DATE)
						+ "</p></font></body></html>";
				currentListOfTasksModel.addElement(element);
			}
			/*
			 * String element =
			 * "<html><head><style>	p.padding {padding-left:0.8cm;} <style/><head/><body style=\"width:280px\"><p class=\"padding\">"
			 * + "<table><tr><td width = \"10\">" + (indexOfTask + 1) +
			 * ".</td><td width = \"133\">" +
			 * currentListOfTasks[indexOfTask].getTimesAsString() + "</td><td>"
			 * + currentListOfTasks[indexOfTask].getTaskName() +
			 * "</td></tr></table>" + "</body></html>";
			 */
			String element = "<html><head><style>	p.padding {padding-left:0.8cm;} <style/><head/><body style=\"width:280px\"><p class=\"padding\">"
					+ (indexOfTask + 1) + ". "
					+ currentListOfTasks[indexOfTask].getTimesAsString()
					+ currentListOfTasks[indexOfTask].getTaskName()
					+ "</body></html>";
			currentListOfTasksModel.addElement(element);
		}
		return currentListOfTasksModel;
	}

	private boolean isSameDay(DateTime firstDate, DateTime secondDate) {
		if (firstDate == secondDate) {
			return true;
		}
		if (firstDate == null && secondDate != null || firstDate != null
				&& secondDate == null) {
			return false;
		}
		firstDate = firstDate.withTimeAtStartOfDay();
		secondDate = secondDate.withTimeAtStartOfDay();
		if (firstDate.compareTo(secondDate) == SAME_TIME) {
			return true;
		}
		return false;
	}

	public String getPreviouslyIssued() {
		return changeRecord.getPrevCommand();
	}

	public String getLaterIssued() {
		return changeRecord.getLaterCommand();
	}
}
