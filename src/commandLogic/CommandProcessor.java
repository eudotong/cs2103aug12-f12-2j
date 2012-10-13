package commandLogic;

import java.io.IOException;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import storage.TaskRecords;
import utility.CommandType;

import exceptions.NothingToRedoException;
import exceptions.NothingToUndoException;

public class CommandProcessor {
	private static final String MESSAGE_ERROR_UNRECOGNISED_COMMAND = "Command not recognised.";
	private static final String MESSAGE_ERROR_UNABLE_TO_UNDO = "There are no commands to undo";
	private static final String MESSAGE_ERROR_UNABLE_TO_REDO = "There are no commands to redo";
	
	private CommandDictionary commandDictionary;
	private ChangeRecord changeRecord;
	private TaskRecords taskRecords;
	
	public CommandProcessor() throws IOException{
		changeRecord = new ChangeRecord();
		commandDictionary = new CommandDictionary();
		taskRecords = new TaskRecords();
	}
	
	public String processCommand(String command){
		CommandType commandType = commandDictionary.getCommandType(command);
		switch (commandType){
		case ADD:
			processAdd();
			break;
		case EDIT:
			processEdit();
			break;
		case MARK:
			processMark();
			break;
		case UNDO:
			return processUndo();
		case REDO:
			return processRedo();
		default:
			return MESSAGE_ERROR_UNRECOGNISED_COMMAND;
		}
		return "";
	}
	public void processAdd(){}
	public void processEdit(){}
	public void processMark(){}
	public String processUndo(){
		try{
			changeRecord.undo();
			return "";
		}catch(NothingToUndoException e){
			return MESSAGE_ERROR_UNABLE_TO_UNDO;
		}
	}
	public String processRedo(){
		try{
			changeRecord.redo();
			return "";
		}catch(NothingToRedoException e){
			return MESSAGE_ERROR_UNABLE_TO_REDO;
		}
	}
}
