package exceptions;

public class NothingToUndoException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "Nothing To Undo";

	public NothingToUndoException() {
		super(DEFAULT_MESSAGE);
	}

	public NothingToUndoException(String msg) {
		super(msg);
	}
}
