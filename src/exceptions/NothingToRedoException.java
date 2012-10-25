package exceptions;

public class NothingToRedoException extends Exception {
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "Nothing To Redo";

	public NothingToRedoException() {
		super(DEFAULT_MESSAGE);
	}

	public NothingToRedoException(String msg) {
		super(msg);
	}
}
