package actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.regex.Pattern;

import interfaces.LancerAction;
import main.Principale;

public class Lien extends Observable implements LancerAction {

	private boolean running;
	private ArrayList<File> htmlFiles, cssFiles;
	private String classe;
	
	public Lien(ArrayList<File> files) {
		this.htmlFiles = new ArrayList<File>();
		this.cssFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
		}
		this.running = false;
	}
	
	@Override
	public void run() {
		lancerActionAll();
		running = false;
		update();
	}

	@Override
	public void fichiersSelectionnes(ArrayList<File> files) {
		lancerAction(files);
	}

	@Override
	public void lancerActionAll() {
		parametrer();
		running = true;
		update();
		for (File file : htmlFiles) {
			try {
				checkLien(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Principale.messageFin("Création des liens finie");
	}

	@Override
	public void lancerAction(ArrayList<File> files) {
		htmlFiles.clear();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
		}
	}

	@Override
	public void parametrer() {
		
	}
	
	private void getClasseLien(){
		
	}
	
	private void checkLien(File file) throws IOException{
		File tmp = new File("tmp");
		BufferedReader br = new BufferedReader(new FileReader(file));
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
		String ligne = "";
		String classe = "Lien";

		while ((ligne = br.readLine()) != null){
			if (ligne.contains("<a")) {
				String balise = "a";
				//Si la balise ne tient pas en une ligne
				if (balise != null && !ligne.contains("</"+balise+">")) {
					String baliseText = ligne;
					ligne = br.readLine();
					while (ligne != null && !ligne.contains("</"+balise+">") && !ligne.contains("<?")){
						baliseText += ligne;
						ligne = br.readLine();
					}
					baliseText += (ligne != null) ? ligne : "";
					bw.write(constructLien(baliseText, classe)+"\r\n");
				}
				//Si la balise tient en une ligne
				else if (balise != null && ligne.contains("</"+balise+">")) {
					bw.write(constructLien(ligne, classe)+"\r\n");
				}else
					bw.write(ligne+"\r\n");
			}
			else 
				bw.write(ligne+"\r\n");
		}
		
		br.close();
		bw.close();

		Principale.fileMove(tmp, file);
		tmp.delete();
	}
	
	private String constructLien(String ligne, String classe){
		ligne = ligne.replace("\r\n", " ");
		int debA = ligne.indexOf("<a");
		int finA = ligne.substring(debA+1).indexOf("</a>") + debA + 1;
		String a = ligne.substring(debA, finA);
		return ligne.substring(0,debA)+
				"<a class=\""+classe+"\" href=\""+getHref(a)+"\">"+
				getText(a)+"</a>"+ligne.substring(finA)
				;
	}
	
	private String getHref(String text){
		text = text.replace("\r\n", " ");
		if (text.contains("href=\"")) {
			int deb = text.indexOf("href=\"") + "href=\"".length();
			int end = text.substring(deb+1).indexOf("\"") + deb +1;
			return text.substring(deb, end);
		}else
			return null;
	}
	
	private String getText(String ligne){
		ligne = ligne.replace("\r\n", " ");
		Pattern p = Pattern.compile("<.*?>", Pattern.DOTALL);
		String[] tab = ligne.split(p.pattern());
		String s = "";
		for (String string : tab) {
			s += string;
		}
		return s;
	}
	
	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void setRunning(boolean b) {
		this.running = b;
	}

	@Override
	public void onDispose() {
		// Ne rien faire
	}
	
	private void update(){
		setChanged();
		notifyObservers();
	}

}
