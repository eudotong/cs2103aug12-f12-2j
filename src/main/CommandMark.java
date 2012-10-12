package main;

public class CommandMark implements Command{
	private static final boolean isReversible = true;
	@Override
	public boolean processCommand() {
		return true;
	}
	public Command reverseCommand() {
		//the reverse command is add
		return null;
	}
	public CommandType getCommandType(){
		return CommandType.MARK;
	}
	public boolean isReversible(){
		return isReversible;
	}
}
