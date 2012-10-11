package main;

//should this be interface/abstract/parent?
public interface Command {
	public void processCommand();
	public Command reverseCommand();
	public boolean isReversible();
	public CommandType getCommandType();
}