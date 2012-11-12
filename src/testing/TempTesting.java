package testing;

import testing.stringGenerator.TextGenerator;

public class TempTesting {

	public static void main(String args[]){
		
		TextGenerator textGenerator = new TextGenerator(100);
		
		String testString = textGenerator.next();
		
		System.out.println(testString);
	}
}
