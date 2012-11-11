/**
 * Change test parameters to do targeted brute testing with random string
 * 
 * @author A0081241U
 */

package testing;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import commandLogic.CommandParser;
import exceptions.CommandCouldNotBeParsedException;
import exceptions.IncorrectDateFormatException;
import exceptions.StartTimeAfterEndTimeException;

import testing.stringGenerator.TextGenerator;
import utilities.Command;
import utilities.Task;

public class CommandParserBruteTest {
	
	private static DateTimeFormatter DATE_FORMATTER = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm");
	private static CommandParser COMMAND_PARSER = new CommandParser();
	private static final String WHITE_SPACE = " ";
	
	private static String randomString;
	private static Command actualCommand;
	private static Task expectedTask;
	

	//Test parameters
	private final static int[] TEST_COMMAND_LENGTH = {30, 50, 70};
	private final static int TEST_ITERATIONS = 50;

	private final static String TEST_COMMAND_TYPE = "add";
	
	private static DateTime TEST_DATE_TIME = DATE_FORMATTER.parseDateTime("24/11/2012 10:00");	
	private final static String TEST_DATE_TIME_STRING = "24th Nov 10am";
	
	public static void main(String args[]){
		
		for (int length : TEST_COMMAND_LENGTH){
			
			TextGenerator textGenerator = new TextGenerator(length);
		
			for (int i = 0; i < TEST_ITERATIONS; i++){
				
				System.out.println("Test length = " + length + "\t Test iteration " + i + " of " + TEST_ITERATIONS);
				
				try {
					randomString = textGenerator.next();
					expectedTask = new Task(randomString, TEST_DATE_TIME, null);
					actualCommand = COMMAND_PARSER.parseCommand(TEST_COMMAND_TYPE + WHITE_SPACE + randomString + WHITE_SPACE + TEST_DATE_TIME_STRING);
				} catch (CommandCouldNotBeParsedException e) {
					e.printStackTrace();
				} catch (StartTimeAfterEndTimeException e) {
					e.printStackTrace();
				} catch (IncorrectDateFormatException e) {
					e.printStackTrace();
				}
				
				
				if (isSuccessful()){
					System.out.println("\"" + randomString + "\" is successful" );
				}			
				
			}

		}
	}
	
	private static boolean isSuccessful(){
		
		boolean result;
		
		String expected = expectedTask.toString();
		String actual = actualCommand.toString();
		
		
		if (expected.compareTo(actual) ==0 ){
			result = true;
		}
		else {			
			System.out.println("Expected: " + expected);
			System.out.println("Actual: " + actual);
			result = false;
		}
		
		return result;
	}
}
