package commandLogic;

import java.io.IOException;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;
import storage.TaskRecords;
import utilities.Command;
import utilities.CommandType;
import exceptions.CommandCouldNotBeParsedException;
import exceptions.NothingToRedoException;
import exceptions.NothingToUndoException;

public class CommandProcessor {
	private static final String MESSAGE_ERROR_UNRECOGNISED_COMMAND = "Command not recognised.";
	private static final String MESSAGE_ERROR_UNABLE_TO_UNDO = "There are no commands to undo";
	private static final String MESSAGE_ERROR_UNABLE_TO_REDO = "There are no commands to redo";

	private CommandDictionary commandDictionary;
	private ChangeRecord changeRecord;
	private TaskRecords taskRecords;
	private CommandParser commandParser;

	public CommandProcessor() throws IOException {
		changeRecord = new ChangeRecord();
		commandDictionary = new CommandDictionary();
		taskRecords = new TaskRecords();
	}

	public String processCommand(String command) {
		try {
			String outputMessage = "";
			Command commandIssued = commandParser.parseCommand(command);
			switch (commandIssued.getCommandType()) {
			case ADD:
				outputMessage = processAdd(commandIssued);
			case EDIT:
				outputMessage = processEdit(commandIssued);
			case MARK:
				outputMessage = processMark(commandIssued);
			case SEARCH:
				outputMessage = processSearch(commandIssued);
			case UNDO:
				outputMessage = processUndo();
			case REDO:
				outputMessage = processRedo();
			}
			changeRecord.add(commandIssued);
			return outputMessage;
		} catch (CommandCouldNotBeParsedException e) {
			return MESSAGE_ERROR_UNRECOGNISED_COMMAND;
		}
	}

	private String processSearch(Command command) {
		return "";
	}

	private String processAdd(Command command) {
		return command.processCommand(taskRecords);
	}

	private String processEdit(Command command) {
		return "";
	}

	private String processMark(Command command) {
		return "";
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
}
