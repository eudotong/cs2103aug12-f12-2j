package commandLogic;

import java.io.IOException;
import storage.TaskRecords;
import utilities.Command;
import utilities.Task;
import exceptions.CommandCouldNotBeParsedException;
import exceptions.NothingToRedoException;
import exceptions.NothingToUndoException;

public class CommandProcessor {
	private static final String EMPTY_STRING = "";
	private static final String NEW_LINE = "\n";
	private static final String MESSAGE_ERROR_UNRECOGNISED_COMMAND = "Command not recognised.";
	private static final String MESSAGE_ERROR_UNABLE_TO_UNDO = "There are no commands to undo";
	private static final String MESSAGE_ERROR_UNABLE_TO_REDO = "There are no commands to redo";

	private ChangeRecord changeRecord;
	private TaskRecords taskRecords;
	private CommandParser commandParser;

	public CommandProcessor() throws IOException {
		changeRecord = new ChangeRecord();
		commandParser = new CommandParser();
		taskRecords = TaskRecords.getInstance();
	}

	public String processCommand(String command) {
		try {
			String outputMessage = EMPTY_STRING;
			Command commandIssued = commandParser.parseCommand(command);
			switch (commandIssued.getCommandType()) {
			case ADD:
				outputMessage = processAdd(commandIssued);
				break;
			case EDIT:
				outputMessage = processEdit(commandIssued);
				break;
			case MARK:
				outputMessage = processMark(commandIssued);
				break;
			case SEARCH:
				outputMessage = processSearch(commandIssued);
				break;
			case UNDO:
				outputMessage = processUndo();
				break;
			case REDO:
				outputMessage = processRedo();
				break;
			}
			changeRecord.add(commandIssued);
			return outputMessage;
		} catch (CommandCouldNotBeParsedException e) {
			return MESSAGE_ERROR_UNRECOGNISED_COMMAND;
		}
	}

	private String processSearch(Command command) {
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
			return MESSAGE_ERROR_UNABLE_TO_UNDO;
		}
	}

	private String processRedo() {
		try {
			Command commandToRedo = changeRecord.redo();
			return commandToRedo.processCommand(taskRecords);
		} catch (NothingToRedoException e) {
			return MESSAGE_ERROR_UNABLE_TO_REDO;
		}
	}

	public String getCurrentListOfTasks() {
		Task[] currentListOfTasks = taskRecords.getCurrentListOfTasks();
		String output = EMPTY_STRING;
		for (int indexOfTask = 0; indexOfTask < currentListOfTasks.length; indexOfTask++) {
			if (indexOfTask == currentListOfTasks.length - 1) {
				output += (indexOfTask + 1) + ". "
						+ currentListOfTasks[indexOfTask];
			} else {
				output += (indexOfTask + 1) + ". "
						+ currentListOfTasks[indexOfTask] + NEW_LINE;
			}
		}
		return output;
	}
}
