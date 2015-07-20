package exceptions;

public class FichierNonTrouve extends MyException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FichierNonTrouve(String fileName) {
		super("Fichier \""+fileName+"\" non trouvé");
		displayMessage();
	}
}
