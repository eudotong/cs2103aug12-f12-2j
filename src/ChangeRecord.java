
public class ChangeRecord {
	private String [] toUndo;
	private String [] toRedo;
	private int undoPointer;
	private int redoPointer;
	
	public ChangeRecord(){
		toUndo = new String [20];
		toRedo = new String [20];
		undoPointer = 0;
		redoPointer = 0;
	}
	
	public void undo(){}
	
	public void redo(){}
}
