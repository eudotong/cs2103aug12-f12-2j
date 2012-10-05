public class CommandDictionary {
	private static final String [] LIST_ADD_SYNONYMS = {"add", "insert"};
	private static final String [] LIST_MARK_SYNONYMS = {"mark", "delete", "del"};
	private static final String [] LIST_EDIT_SYNONYMS = {"update", "edit", "change"};
	private static final String [] LIST_REDO_SYNONYMS = {"redo"};
	private static final String [] LIST_UNDO_SYNONYMS = {"undo"};
	
	public EnumCommandType getCommandType(String command){
		for(String entry : LIST_ADD_SYNONYMS){
			if(command.contains(entry)){
				return EnumCommandType.ADD;
			}
		}
		for(String entry :  LIST_EDIT_SYNONYMS){
			if(command.contains(entry)){
				return EnumCommandType.EDIT;
			}
		}
		for(String entry :  LIST_MARK_SYNONYMS){
			if(command.contains(entry)){
				return EnumCommandType.MARK;
			}
		}
		for(String entry :  LIST_UNDO_SYNONYMS){
			if(command.contains(entry)){
				return EnumCommandType.UNDO;
			}
		}
		for(String entry :  LIST_REDO_SYNONYMS){
			if(command.contains(entry)){
				return EnumCommandType.REDO;
			}
		}
		return EnumCommandType.NO_SUCH_COMMAND;
	}
}
