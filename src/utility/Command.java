package utility;

public interface Command {
	public boolean processCommand();
	public Command reverseCommand();
	public boolean isReversible();
	public CommandType getCommandType();
}