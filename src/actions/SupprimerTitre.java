package actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Document;

import interfaces.Action;

public class SupprimerTitre extends Action {

	private boolean isBody;
	private int cpt;
	
	public SupprimerTitre(ArrayList<File> files) {
		super(files);
		messageFin = "Titres supprimés";
		intitule = "Supprimer les titres";
		isBody = false;
		cpt = 5;
	}

//	@Override
//	protected void applyStyle(BufferedReader br, BufferedWriter bw, String ligne) throws IOException {
//		if (ligne.contains("<body"))
//			isBody = true;
//		else if (ligne.contains("</body>"))
//			isBody = false;
//		
//		if (isBody && !ligne.contains("<h1") && cpt > 0) {
//			cpt--;
//			bw.write(ligne+"\r\n");
//		}
//		else if (isBody && ligne.contains("<h1") && cpt > 0) {
//			bw.write(getH1(ligne)+"\r\n");
//		}
//		else
//			bw.write(ligne+"\r\n");
//		
//	}
	
	private String getH1(String ligne){
		int deb = ligne.indexOf("<h1") ;
		int fin = ligne.indexOf("</h1>") + "</h1>".length();
		if (deb > 0) 
			return ligne.subSequence(0, deb)+ligne.substring(fin);
		else
			return ligne.substring(fin);
	}

@Override
protected Document applyStyle(Document doc) throws IOException {
	// TODO Auto-generated method stub
	return null;
}
}
