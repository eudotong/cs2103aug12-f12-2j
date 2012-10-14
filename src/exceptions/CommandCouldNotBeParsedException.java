package exceptions;

public class CommandCouldNotBeParsedException extends Exception{
	private static final long serialVersionUID = 1L;
	private static final String DEFAULT_MESSAGE = "The command could not be parsed";
	public CommandCouldNotBeParsedException(){
		super(DEFAULT_MESSAGE);
	}
	public CommandCouldNotBeParsedException(String msg){
		super(msg);
	}
}
