//should this be interface/abstract/parent?
public interface Command {
	public void processCommand();
	public Command reverseCommand();
}