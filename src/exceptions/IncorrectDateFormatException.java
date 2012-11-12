package exceptions;

public class IncorrectDateFormatException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "Incorrect Date Format.";

	public IncorrectDateFormatException() {
		super(DEFAULT_MESSAGE);
	}

	public IncorrectDateFormatException(String msg) {
		super(msg);
	}
}
