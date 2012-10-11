package main;

public class CommandSearch implements Command{
	private static final boolean isReversible = true;
	@Override
	public void processCommand() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public Command reverseCommand() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public CommandType getCommandType() {
		return CommandType.SEARCH;
	}
	public boolean isReversible(){
		return isReversible;
	}

}
