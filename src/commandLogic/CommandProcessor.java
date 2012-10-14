package commandLogic;

import java.io.IOException;
import java.util.logging.Logger;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.joda.time.format.DateTimeParser;

import storage.TaskRecords;
import utilities.Command;
import utilities.CommandType;

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
		return "command processed";
	}
	private String processSearch(Command command){
		return "";
	}
	private String processAdd(Command command){return "";}
	private String processEdit(Command command){return "";}
	private String processMark(Command command){return "";}
	private String processUndo(){
		try{
			Command commandToUndo = changeRecord.undo();
			boolean isUndone = commandToUndo.processCommand(taskRecords);
			if(isUndone){
				return "";
			}
			return "";
		}catch(NothingToUndoException e){
			return MESSAGE_ERROR_UNABLE_TO_UNDO;
		}
	}
	private String processRedo(){
		try{
			Command commandToRedo = changeRecord.redo();
			boolean isRedone = commandToRedo.processCommand(taskRecords);
			if(isRedone){
				return "";
			}
			return "";
		}catch(NothingToRedoException e){
			return MESSAGE_ERROR_UNABLE_TO_REDO;
		}
	}
}
