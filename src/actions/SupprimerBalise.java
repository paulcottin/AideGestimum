package actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JOptionPane;

import interfaces.LancerAction;
import main.Principale;

public class SupprimerBalise extends Observable implements LancerAction{

	private ArrayList<File> htmlFiles;
	private String balise;
	private boolean running;

	public SupprimerBalise(ArrayList<File> files) {
		this.htmlFiles = new ArrayList<File>();
		this.running = false;
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
		}
	}
	
	public void run() {
		lancerActionAll();	
		running = false;
		update();
	}

	@Override
	public void lancerActionAll() {
		parametrer();
		running = true;
		update();
		if (balise != null) {
			suppBalise();
			Principale.messageFin("Balise '"+balise+"' supprimée avec succès");
		}else
			Principale.messageFin("Il faut rentrer un nom de balise");
	}
	
	@Override
	public void fichiersSelectionnes(ArrayList<File> files) {
		lancerAction(files);
	}

	@Override
	public void lancerAction(ArrayList<File> files) {
		htmlFiles.clear();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"));
			htmlFiles.add(file);
		}
	}
	
	@Override
	public void parametrer(){
		getBalise();
	}

	private void getBalise(){
		balise = JOptionPane.showInputDialog(null, "Nom de la balise", "Supprimer des balises", JOptionPane.QUESTION_MESSAGE);
	}

	private void suppBalise(){
		for (File file : htmlFiles) {
			try {
				suppBaliseHelper(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void suppBaliseHelper(File f) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(f));
		File tmp = new File(f.getAbsolutePath()+"_tmp");
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));

		String ligne = "";

		while ((ligne = br.readLine()) != null){
			//Si on tombe sur la balise recherchée on ne la réecrit pas
			if (ligne.contains("<"+balise) && ligne.contains("</"+balise+">")){
				int debut = ligne.indexOf("<"+balise);
				int fin = debut + ligne.substring(debut).indexOf("</"+balise+">") + ("</"+balise+">").length();
				String avant = ligne.substring(0, debut);
				String apres = ligne.substring(fin, ligne.length());
				bw.write(avant+apres+"\r\n");
			}
			//Sinon on réécrit
			else
				bw.write(ligne+"\r\n");
		}

		br.close();
		bw.close();

		Principale.fileMove(tmp, f);
		tmp.delete();
	}
	
	private void update(){
		setChanged();
		notifyObservers();
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
		//Ne rien faire
	}
}


