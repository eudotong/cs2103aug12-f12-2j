package commandLogic;

import java.util.HashMap;

import org.joda.time.DateTime;

import utilities.Command;
import utilities.CommandType;

import exceptions.CommandCouldNotBeParsedException;

public class CommandParser {
	private static final String [] LIST_ADD_SYNONYMS = {"add", "insert", "create", "new", "put"};
	private static final String [] LIST_MARK_SYNONYMS = {"mark", "delete", "del", "remove", "discard", "erase", "drop"};
	private static final String [] LIST_EDIT_SYNONYMS = {"update", "edit", "change", "alter", "modify"};
	private static final String [] LIST_SEARCH_SYNONYMS = {"find", "display", "search", "show"};
	private static final String [] LIST_REDO_SYNONYMS = {"redo"};
	private static final String [] LIST_UNDO_SYNONYMS = {"undo"};
	private HashMap<String, CommandType> dictionary;
	
	public CommandParser(){
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
		for(String entry :  LIST_SEARCH_SYNONYMS){
			dictionary.put(entry, CommandType.SEARCH);
		}
		for(String entry :  LIST_UNDO_SYNONYMS){
			dictionary.put(entry, CommandType.UNDO);
		}
		for(String entry :  LIST_REDO_SYNONYMS){
			dictionary.put(entry, CommandType.REDO);
		}
	}
	
	public Command parseCommand(String command) throws CommandCouldNotBeParsedException{
		//parse the command
		//find out the CommandType (using the dictionary)
		//split up the command to find these components:
		//taskName;startTime;endTime;isImportant; (refer to task class)
		//create a new Task with these components (some may be null/not defined by user, you
		//can use an appropriate constructor in the Task class.)
		//create a new Command Object based on the CommandType (CommandAdd, CommandEdit etc)
		//return the Command object.
		//In the event that you cannot parse the command, throw CommandCouldNotBeParsedException
		//we can omit parsing edit commands first
		//note: when parsing commands make sure that it is not case-sensitive
		return null;
	}
	
	public CommandType getCommandType(String command){
		String [] wordsInCommand = command.split("\\W");
		if (dictionary.containsKey(wordsInCommand[0])) {
			return dictionary.get(wordsInCommand[0]);
		}
		
		//TODO Please change so that it throws an exception inside
		//throw CommandCouldNotBeParsed
		return CommandType.ADD;
	}
}
