package main;

public class CommandEdit implements Command{
	private static final boolean isReversible = true;
	public Command reverseCommand() {
		//the reverse command is edit
		return null;
	}

	@Override
	public boolean processCommand() {
		return true;
		
	}
	public boolean isReversible(){
		return isReversible;
	}
	public CommandType getCommandType(){
		return CommandType.EDIT;
	}
}
