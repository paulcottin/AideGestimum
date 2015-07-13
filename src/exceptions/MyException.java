package exceptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

public abstract class MyException {

	protected String message;
	protected File log;
	
	public MyException() {
		this.message = "";
		this.log = new File("log.html");
	}
	
	public MyException(String message){
		this.message = message;
		this.log = new File("log.html");
	}
	
	protected void displayMessage(){
		writeLog();
		JOptionPane.showMessageDialog(null, "<html>"+message+"<br/> "
				+ "(Fichier \"log.html\" créé dans le répertoire du jar pour l'historique des erreurs)</html>", "Erreur", JOptionPane.ERROR_MESSAGE);
	}
	
	private void writeLog(){
		BufferedWriter bw;
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(log));
			String ligne = "", fichier = "";
			
			while ((ligne = br.readLine()) != null)
				fichier += ligne;
			
			br.close();
			
			bw = new BufferedWriter(new FileWriter(log));
			bw.write(fichier);
			
			bw.write(GregorianCalendar.getInstance().getTime().toString()+" : "+message+"<br/>");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
