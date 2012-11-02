package testing;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

public class Testing {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String s = "something 12";
		Pattern anyNumberPattern = Pattern.compile("\\d+$");
		Matcher patternMatcher = anyNumberPattern.matcher(s);
		System.out.println(patternMatcher.find());
		System.out.println(patternMatcher.group(0));
	}

}
