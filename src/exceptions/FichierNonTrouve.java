package exceptions;

public class FichierNonTrouve extends MyException{

	public FichierNonTrouve(String fileName) {
		super("Fichier \""+fileName+"\" non trouvé");
		displayMessage();
	}
}
