package testing.stringGenerator;

import java.util.HashMap;
import java.util.Random;


public class MarkovModel {

	//No possible next character - indicator
	final char NOCHARACTER = (char)(255);
	

	//Object Attributes
	int myOrder = 0;
	HashMap<String,Integer[]> table = new HashMap<String,Integer[]>();
	Random generator = new Random(); //For generating random number
	
	// Index for number of times the KEY appears
	final int FREQ_OF_KEY = 128;
	
	//Constructor - assumes order will be at least 1
	MarkovModel(String text, int order){
		
		//Exceptions
		int length = text.length();
		if (length == 0 || order < 1)
			throw new IllegalArgumentException();
		
		// Setting attribute
		myOrder = order;
		
		//Mapping in progress
		int startIndex=0, endIndex=startIndex + order;
		String subText; 
		char nextChar;
		Integer[] freqArray;
		
		while (endIndex < text.length()){
			

			subText = text.substring(startIndex,endIndex);
			nextChar = text.charAt(endIndex);
			
			// Check if char is between 0 to 127
			if ((int)nextChar<0 || (int)nextChar>127){
				
				//Skips this chunk all together
				startIndex=endIndex+1;
				endIndex=startIndex +order;
				continue;
			}
			
			// Check if this subText is already in table
			if (table.containsKey(subText)){
				
				//Retrieve the freqArray
				freqArray=table.get(subText);
				
				//Set the freqArray
				freqArray[nextChar]++;
	
			}
			
			// If the subText is not in table, insert it with new array
			else{
			
				// Initialise a frequency array. Last slot is for frequency of the Key itself
				freqArray = new Integer[129];
				
				for (int i=0; i<129; i++){
					freqArray[i]=0;
				}
				
				//Set the freqArray
				freqArray[nextChar]++;
			}
			
			// Create or override the key/value

			freqArray[FREQ_OF_KEY]++; 	//Note that the Frequency of the last subString is not recorded - this is intentional for the purpose of this implementation
			table.put(subText, freqArray);
			

			startIndex++;
			endIndex++;
			
			/*** FOR DEBUG ***
			System.out.println("SubString is: " + subText);
			System.out.println("FREQ OF KEY: " + freqArray[FREQ_OF_KEY]);
			System.out.println("Next Character is: " + nextChar  + " of value: " + (int)nextChar);
			System.out.println("FreqArray[nextChar] = " + freqArray[nextChar]);
			
			System.out.println();
			***/
		}
	}
	
	// Returns the order of the Markov model
	int order(){
		
		return myOrder;
	}
	
	
	int getFrequency(String kgram){
		
		// Check length of string
		if (kgram.length()!=myOrder)
			throw new IllegalArgumentException("String not of length == order");
		
		// In the event that kgram is not mapped, then skip it
		Integer[] array = table.get(kgram);
		if(array==null){
			return 0;
		}
		
		return array[FREQ_OF_KEY];
	}
	
	int getFrequency (String kgram, char c){
		
		// Check length of string
		if (kgram.length()!=myOrder)
			throw new IllegalArgumentException("String not of length == order");
		
		Integer[] array = table.get(kgram);
		
		/*** FOR DEBUG ***
		for (int i=0; i<array.length; i++){
			System.out.println("index: " + i + " - " + array[i]);
		}
		
		System.out.println("c is " + (int)c);
		***/
		
		return array[(int)c];
	}
	
	// Gives a random character with probability of "getFreq(kgram,c)/getFreq(kgram)" - O(n) solution
	char nextCharacter (String kgram){
		
		// Check length of string
		if (kgram.length()!=myOrder)
			throw new IllegalArgumentException("String not of length == order");
		
		// If there's no probability of having any next character i.e: No freq (or is last subString of the order)
		int totalFreq = getFrequency(kgram);
		if (totalFreq == 0)
			return NOCHARACTER;
		
		// generate random index
		int randIndex = generator.nextInt(totalFreq)+1; //I don't want zero but I want the totalFreq
		
		//Choose the char - This is the O(n) part
		// Retrieve the freqArray
		Integer[] array = table.get(kgram);
		int selected = 0;
		for (int i=0; i<128; i++){
			
			// Cummulative probability approach
			if (array[i] != 0){
				selected +=array[i];
			}
			
			//Found it
			if (selected>=randIndex){
				return (char)i;
			}
		
		}
		
		//Theoretically this should reach this stage
		System.out.println("SOMETHING'S FISHY");
		return NOCHARACTER;
	}
	
	// Set random seed to be used by random number generator
	void setRandomSeed(long s){
		
		generator.setSeed(s);
		
	}
	
	// TESTING
	public static void main(String[] args){
		MarkovModel model = new MarkovModel("abdacabdacbdabdacda", 1);
		System.out.println(model.getFrequency("d",'a'));
	}
}
