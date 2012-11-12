/**
 * TextGenerator generates a pseudo-random string
 * 
 * @author A0081241U
 */

package testing.stringGenerator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TextGenerator {
	
	private final static String SOURCE_TEXT_NAME = "sampleText.in";
	private final static int DEFAULT_ORDER = 5;
	private final static int DEFAULT_NUM_OF_CHAR = 30;
	
	
	MarkovModel charGenerator;
	String seedString;
	String markovSourceText;
	private int markovOrder, numOfChar;
	
	public TextGenerator (){
		this(DEFAULT_ORDER,DEFAULT_NUM_OF_CHAR);
	}
	
	public TextGenerator (int length){
		this(DEFAULT_ORDER, length);
	}
	
	public TextGenerator (int order, int length){
		
		markovOrder = order;
		numOfChar = length;
		
		/*
		 * Reads in the file
		 */
		File filename = new File(SOURCE_TEXT_NAME);
		BufferedReader bufferedDataFile;
		markovSourceText = "";
		String temp = "";
		
		try{
			FileReader dataFile = new FileReader(filename);
			bufferedDataFile = new BufferedReader(dataFile);
		
			temp = bufferedDataFile.readLine();
			
			while (temp!=null){
				//temp.replace("\n","");
				markovSourceText = markovSourceText.concat(temp);
				//System.out.println(inputText);
				temp = bufferedDataFile.readLine();
			} 
			
			bufferedDataFile.close();
		}
		
		catch (IOException e) {
			e.printStackTrace();
			return;
		}
		
		// Creates Markov
		charGenerator = new MarkovModel(markovSourceText, markovOrder);

		seedString = markovSourceText.substring(0, markovOrder);
		
		
		
	}
	
	public String next(){
		
		char newChar;
		StringBuilder stringBuilder = new StringBuilder();
		
		// Starts printing
		for (int i=0; i<numOfChar; i++){
			
			newChar = charGenerator.nextCharacter(seedString);
			
			
			//Reset seedString
			if (newChar == charGenerator.NOCHARACTER){
				seedString = markovSourceText.substring(0, markovOrder);
				newChar = charGenerator.nextCharacter(seedString);
			}
			else{
				//Changes seedString
				seedString = seedString.substring(1,markovOrder);
				seedString = seedString.concat(Character.toString(newChar));
			}
			
			stringBuilder.append(newChar);
		}
		
		String outputString = stringBuilder.toString().trim();
		return outputString;
	}
}
