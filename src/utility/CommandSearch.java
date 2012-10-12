package utility;


public class CommandSearch implements Command{
	private static final boolean isReversible = false;
	@Override
	public boolean processCommand() {
		// TODO Auto-generated method stub
		return true;
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
