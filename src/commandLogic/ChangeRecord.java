package commandLogic;

import java.util.Stack;

import utilities.Command;

import exceptions.NothingToRedoException;
import exceptions.NothingToUndoException;

public class ChangeRecord {
	private static final String EMPTY_STRING = "";
	private Stack<Command> toRedoStack;
	private Stack<Command> toUndoStack;
	private Stack<String> prevCommandsStack;
	private Stack<String> laterCommandsStack;

	public ChangeRecord() {
		toRedoStack = new Stack<Command>();
		toUndoStack = new Stack<Command>();
		prevCommandsStack = new Stack<String>();
		laterCommandsStack = new Stack<String>();
	}

	public void add(String newCommand) {
		if (!newCommand.isEmpty()) {
			prevCommandsStack.push(newCommand);
			laterCommandsStack.clear();
		}
	}

	public void add(Command newCommand) {
		if (newCommand.isReversible()) {
			toUndoStack.push(newCommand);
			toRedoStack.clear();
		}
	}

	public String getPrevCommand() {
		if (prevCommandsStack.isEmpty()) {
			return EMPTY_STRING;
		}
		String previousCommand = prevCommandsStack.pop();
		laterCommandsStack.push(previousCommand);
		return previousCommand;
	}

	public String getLaterCommand() {
		if (laterCommandsStack.isEmpty()) {
			return EMPTY_STRING;
		}
		String laterCommand = laterCommandsStack.pop();
		prevCommandsStack.push(laterCommand);
		return laterCommand;
	}

	public Command undo() throws NothingToUndoException {
		if (toUndoStack.isEmpty()) {
			throw new NothingToUndoException();
		}
		Command commandToUndo = toUndoStack.pop();
		Command reverseCommand = commandToUndo.reverseCommand();
		toRedoStack.push(commandToUndo);
		return reverseCommand;
	}

	public Command redo() throws NothingToRedoException {
		if (toRedoStack.isEmpty()) {
			throw new NothingToRedoException();
		}
		Command commandToRedo = toRedoStack.pop();
		toUndoStack.push(commandToRedo);
		return commandToRedo;
	}
}