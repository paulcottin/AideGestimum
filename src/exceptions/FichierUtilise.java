package exceptions;

public class FichierUtilise extends MyException{
	
	public FichierUtilise(String fileName) {
		super("Fichier \""+fileName+"\" utilis� par un autre processus, veuillez fermer ce programme");
		displayMessage();
	}

}
