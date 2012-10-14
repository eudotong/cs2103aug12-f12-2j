package commandLogic;
import java.util.Stack;

import utilities.Command;

import exceptions.NothingToRedoException;
import exceptions.NothingToUndoException;

public class ChangeRecord {
	private Stack<Command>toRedoStack;
	private Stack<Command>toUndoStack;
	
	public ChangeRecord(){
		toRedoStack = new Stack<Command>();
		toUndoStack = new Stack<Command>();
	}

	public void add(Command newCommand){
		if(newCommand.isReversible()){
			toUndoStack.push(newCommand);
			toRedoStack.clear();
		}
	}
	
	public Command undo() throws NothingToUndoException{
		if(toUndoStack.isEmpty()){
			throw new NothingToUndoException();
		}
		Command commandToUndo = toUndoStack.pop();
		Command reverseCommand = commandToUndo.reverseCommand();
		toRedoStack.push(commandToUndo);
		return reverseCommand;
	}
	
	public Command redo() throws NothingToRedoException{
		if(toRedoStack.isEmpty()){
			throw new NothingToRedoException();
		}
		Command commandToRedo = toRedoStack.pop();
		toUndoStack.push(commandToRedo);
		return commandToRedo;
	}
}