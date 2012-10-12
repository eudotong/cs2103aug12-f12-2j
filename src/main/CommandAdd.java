package main;
public class CommandAdd implements Command{
	private static final boolean isReversible = true;
	private Task taskToBeAdded;
	public CommandAdd(String command){
		
	}
	public Command reverseCommand(){
		return null;
	}
	public boolean processCommand() {
		return true;
	}
	public CommandType getCommandType(){
		return CommandType.ADD;
	}
	public boolean isReversible(){
		return isReversible;
	}
}
