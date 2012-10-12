package main;
public class CommandAdd implements Command{
	private static final boolean isReversible = true;
	public Command reverseCommand(){
		return null;
	}
	public void processCommand() {
		// TODO Auto-generated method stub
		
	}
	public CommandType getCommandType(){
		return CommandType.ADD;
	}
	public boolean isReversible(){
		return isReversible;
	}
}
