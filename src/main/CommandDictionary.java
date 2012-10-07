package main;
public class CommandDictionary {
	private static final String [] LIST_ADD_SYNONYMS = {"add", "insert"};
	private static final String [] LIST_MARK_SYNONYMS = {"mark", "delete", "del"};
	private static final String [] LIST_EDIT_SYNONYMS = {"update", "edit", "change"};
	private static final String [] LIST_REDO_SYNONYMS = {"redo"};
	private static final String [] LIST_UNDO_SYNONYMS = {"undo"};
	
	public CommandType getCommandType(String command){
		for(String entry : LIST_ADD_SYNONYMS){
			if(command.contains(entry)){
				return CommandType.ADD;
			}
		}
		for(String entry :  LIST_EDIT_SYNONYMS){
			if(command.contains(entry)){
				return CommandType.EDIT;
			}
		}
		for(String entry :  LIST_MARK_SYNONYMS){
			if(command.contains(entry)){
				return CommandType.MARK;
			}
		}
		for(String entry :  LIST_UNDO_SYNONYMS){
			if(command.contains(entry)){
				return CommandType.UNDO;
			}
		}
		for(String entry :  LIST_REDO_SYNONYMS){
			if(command.contains(entry)){
				return CommandType.REDO;
			}
		}
		return CommandType.NO_SUCH_COMMAND;
	}
}
