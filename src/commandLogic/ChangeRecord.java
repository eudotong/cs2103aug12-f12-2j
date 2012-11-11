package commandLogic;

import java.util.Stack;

import utilities.Command;

import exceptions.NothingToRedoException;
import exceptions.NothingToUndoException;

/**
 * ChangeRecord is used to record previously issued user commands. This
 * facilitates undo/redo commands. It makes use of two stacks to record changes.
 * 
 * @author A0088278L
 */
public class ChangeRecord {
	private static final String EMPTY_STRING = "";
	private Stack<Command> toRedoStack;
	private Stack<Command> toUndoStack;
	private Stack<String> prevCommandsStack;
	private Stack<String> laterCommandsStack;

	/**
	 * Initializes a newly created ChangeRecord object.
	 */
	public ChangeRecord() {
		toRedoStack = new Stack<Command>();
		toUndoStack = new Stack<Command>();
		prevCommandsStack = new Stack<String>();
		laterCommandsStack = new Stack<String>();
	}

	/**
	 * Adds new command issued into the previous commands stack. Later commands
	 * stack is cleared.
	 * 
	 * @param newCommand
	 */
	public void add(String newCommand) {
		if (!newCommand.isEmpty()) {
			prevCommandsStack.push(newCommand);
			laterCommandsStack.clear();
		}
	}

	/**
	 * Adds new command issued into the undo stack if it is reversible. Redo
	 * stack is cleared.
	 * 
	 * @param newCommand
	 */
	public void add(Command newCommand) {
		if (newCommand.isReversible()) {
			toUndoStack.push(newCommand);
			toRedoStack.clear();
		}
	}

	/**
	 * Returns top of the previous commands stack. If no previous commands were
	 * issued, returns an empty string.
	 * 
	 * @return String
	 */
	public String getPrevCommand() {
		if (prevCommandsStack.isEmpty()) {
			return EMPTY_STRING;
		}
		String previousCommand = prevCommandsStack.pop();
		laterCommandsStack.push(previousCommand);
		return previousCommand;
	}

	/**
	 * Returns top of the later commands stack. If no later commands were
	 * issued, returns an empty string.
	 * 
	 * @return String
	 */
	public String getLaterCommand() {
		if (laterCommandsStack.isEmpty()) {
			return EMPTY_STRING;
		}
		String laterCommand = laterCommandsStack.pop();
		prevCommandsStack.push(laterCommand);
		return laterCommand;
	}

	/**
	 * Returns top of the undo stack. If the undo stack is empty,
	 * NothingToUndoException is thrown.
	 * 
	 * @return Command
	 * @throws NothingToUndoException
	 */
	public Command undo() throws NothingToUndoException {
		if (toUndoStack.isEmpty()) {
			throw new NothingToUndoException();
		}
		Command commandToUndo = toUndoStack.pop();
		Command reverseCommand = commandToUndo.reverseCommand();
		toRedoStack.push(commandToUndo);
		return reverseCommand;
	}

	/**
	 * Returns top of the redo stack. If the redo stack is empty,
	 * NothingToRedoException is thrown.
	 * 
	 * @return Command
	 * @throws NothingToRedoException
	 */
	public Command redo() throws NothingToRedoException {
		if (toRedoStack.isEmpty()) {
			throw new NothingToRedoException();
		}
		Command commandToRedo = toRedoStack.pop();
		toUndoStack.push(commandToRedo);
		return commandToRedo;
	}
}