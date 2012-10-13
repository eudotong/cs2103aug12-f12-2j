package commandLogic;

import java.util.HashMap;

import utility.CommandType;


public class CommandDictionary {
	private static final String [] LIST_ADD_SYNONYMS = {"add", "insert", "create", "new"};
	private static final String [] LIST_MARK_SYNONYMS = {"mark", "delete", "del", "remove", "discard", "erase", "drop"};
	private static final String [] LIST_EDIT_SYNONYMS = {"update", "edit", "change", "alter", "modify"};
	private static final String [] LIST_REDO_SYNONYMS = {"redo"};
	private static final String [] LIST_UNDO_SYNONYMS = {"undo"};
	private HashMap<String, CommandType> dictionary;
	
	public CommandDictionary(){
		initialiseDictionary();
	}
	private void initialiseDictionary() {
		dictionary = new HashMap<String, CommandType>();
		for(String entry : LIST_ADD_SYNONYMS){
			dictionary.put(entry, CommandType.ADD);
		}
		for(String entry :  LIST_EDIT_SYNONYMS){
			dictionary.put(entry, CommandType.EDIT);
		}
		for(String entry :  LIST_MARK_SYNONYMS){
			dictionary.put(entry, CommandType.MARK);
		}
		for(String entry :  LIST_UNDO_SYNONYMS){
			dictionary.put(entry, CommandType.UNDO);
		}
		for(String entry :  LIST_REDO_SYNONYMS){
			dictionary.put(entry, CommandType.REDO);
		}
	}
	
	public CommandType getCommandType(String command){
		String [] wordsInCommand = command.split("\\W");
		if (dictionary.containsKey(wordsInCommand[0])) {
			return dictionary.get(wordsInCommand[0]);
		}
		
		//TODO What if it's not first word? Can this split be reused?
		return CommandType.NO_SUCH_COMMAND;
	}
}
