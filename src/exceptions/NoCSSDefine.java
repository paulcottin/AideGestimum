package exceptions;

public class NoCSSDefine extends MyException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public NoCSSDefine(String message) {
		super(message);
	}
	
	public void printMessage(){
		displayMessage();
	}

}
