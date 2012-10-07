package main;

import exceptions.NothingToRedoException;
import exceptions.NothingToUndoException;

public class CommandProcessor {
	private static final String MESSAGE_ERROR_UNRECOGNISED_COMMAND = "Command not recognised.";
	private static final String MESSAGE_ERROR_UNABLE_TO_UNDO = "There are no commands to undo";
	private static final String MESSAGE_ERROR_UNABLE_TO_REDO = "There are no commands to redo";
	
	private CommandDictionary commandDictionary;
	private ChangeRecord changeRecord;
	
	public CommandProcessor(){
		changeRecord = new ChangeRecord();
		commandDictionary = new CommandDictionary();
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
			Command commandToProcess = changeRecord.undo();
			commandToProcess.processCommand();
			return "";
		}catch(NothingToUndoException e){
			return e.getMessage();
		}
	}
	public String processRedo(){
		try{
			Command commandToProcess = changeRecord.redo();
			commandToProcess.processCommand();
			return "";
		}catch(NothingToRedoException e){
			return e.getMessage();
		}
	}
}
