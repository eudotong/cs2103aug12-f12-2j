public class ChangeRecord {
	private static final int STACK_SIZE = 20;
	private CommandStack toUndo;
	private CommandStack toRedo;
	
	public ChangeRecord(){
		toUndo = new CommandStack(STACK_SIZE);
		toRedo = new CommandStack(STACK_SIZE);
	}

	public void add(Command newCommand){
		toUndo.push(newCommand);
		toRedo.clear();
	}
	
	public Command undo(){
		Command commandToUndo = toUndo.pop();
		if(commandToUndo == null){
			return null;
		}
		Command reverseCommand = commandToUndo.reverseCommand();
		toRedo.push(commandToUndo);
		return reverseCommand;
	}
	
	public Command redo(){
		Command commandToRedo = toRedo.pop();
		if(commandToRedo == null){
			return null;
		}
		Command reverseCommand = commandToRedo.reverseCommand();
		toUndo.push(reverseCommand);
		return commandToRedo;
	}
}

class CommandStack{
	private int stackSize;
	private Command[] commandStack;
	private int topPointer; //points to the top of the stack
	public CommandStack(int stackSize){
		this.stackSize = stackSize;
		commandStack = new Command[stackSize];
		topPointer = 0;
	}
	
	public void push(Command newCommand){
		commandStack[topPointer] = newCommand;
		topPointer = (topPointer + 1) % stackSize;
	}
	
	public Command pop(){
		topPointer = (topPointer + stackSize - 1) % stackSize;
		Command poppedCommand = commandStack[topPointer];
		commandStack[topPointer] = null;
		return poppedCommand;
	}
	
	public void clear(){
		commandStack = new Command[stackSize];
		topPointer = 0;
	}
}