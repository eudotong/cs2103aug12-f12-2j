package main;
import java.util.Stack;

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
	
	public boolean undo() throws NothingToUndoException{
		if(toUndoStack.isEmpty()){
			throw new NothingToUndoException();
		}
		Command commandToUndo = toUndoStack.pop();
		Command reverseCommand = commandToUndo.reverseCommand();
		toRedoStack.push(commandToUndo);
		reverseCommand.processCommand();
		return true;
	}
	
	public boolean redo() throws NothingToRedoException{
		if(toRedoStack.isEmpty()){
			throw new NothingToRedoException();
		}
		Command commandToRedo = toRedoStack.pop();
		toUndoStack.push(commandToRedo);
		return true;
	}
}