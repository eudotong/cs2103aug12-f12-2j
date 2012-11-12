package exceptions;

public class StartTimeAfterEndTimeException extends Exception{
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "Start time is after end time.";

	public StartTimeAfterEndTimeException() {
		super(DEFAULT_MESSAGE);
	}

	public StartTimeAfterEndTimeException(String msg) {
		super(msg);
	}
}
