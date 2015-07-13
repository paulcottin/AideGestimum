package actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

import interfaces.LancerAction;

public class Titre extends Observable implements LancerAction {

	private ArrayList<File> htmlFiles, cssFiles;
	private boolean running;
	private String titreBalise;


	public Titre(ArrayList<File> files) {
		this.htmlFiles = new ArrayList<File>();
		this.cssFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
			else if (file.getAbsolutePath().endsWith(".css"))
				cssFiles.add(file);
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
	public void lancerActionAll() {
		parametrer();
		for (File file : htmlFiles) {
			try {
				applyStyle(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void lancerAction(ArrayList<File> files) {
		htmlFiles.clear();
		htmlFiles.addAll(files);
		lancerActionAll();
	}

	@Override
	public void fichiersSelectionnes(ArrayList<File> files) {
		lancerAction(files);
	}

	@Override
	public void parametrer() {
		
	}
	
	private void applyStyle(File f) throws IOException{
		File tmp = new File("tmp");
		BufferedReader br = new BufferedReader(new FileReader(f));
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
		String ligne = "";
		titreBalise = "h2";

		while ((ligne = br.readLine()) != null){
			if (ligne.matches("<p .*border-bottom:")) {
				System.out.println(ligne);
			}
		}
		
	}

	private void getTitre(File f){
		
	}
	
	private String getCSSFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		
		String ligne = "";
		
		while (!(ligne = br.readLine()).contains("</head>")){
			if (ligne.equals("<link rel=\"stylesheet\"")){
				int deb = ligne.indexOf("<link rel=\"stylesheet\" href=\"") + "<link rel=\"stylesheet\" href=\"".length();
				int fin = ligne.substring(deb+1).indexOf("\"");
				System.out.println(ligne.substring(deb, fin));
				return ligne.substring(deb, fin);
			}
		}
		return null;
	}

	/**
	 * Récupère les classes/balises d'un document CSS
	 * @param cssFile
	 * @return
	 * @throws IOException 
	 */
	private ArrayList<String> getCSSclasses(File file) throws IOException{
		ArrayList<String> reponse = new ArrayList<String>();
		BufferedReader br = new BufferedReader(new FileReader(file));

		String ligne = "";
		while((ligne = br.readLine()) != null){
			if (ligne.contains("{")) {
				String classe = ligne.substring(0, ligne.indexOf("{")-1);
				if (classe.contains("."))
					classe = classe.split("\\.")[1];
				reponse.add(classe);
			}
		}

		br.close();
		return reponse;
	}

	/**
	 * Récupère le path d'une page principale en fonction de son nom ou un path
	 * @param path
	 * @return
	 */
	private String getAbsolutePathPP(String path){
		//Si un path est donné en paramètre on récupère le nom
		if (path.contains("\\")) {
			String[] t = path.split("\\");
			path = t[t.length-1];
		}
		//On renvoi le chemin absolu
		for (File file : cssFiles) {
			if (file.getName().equals(path)) {
				return file.getAbsolutePath();
			}
		}
		return null;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public void setRunning(boolean b) {
		running = b;
	}

	@Override
	public void onDispose() {
		//Ne rien faire
	}

	public void update(){
		setChanged();
		notifyObservers();
	}

}
