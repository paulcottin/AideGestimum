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
		
		Principale.messageFin("Traitement des titres fini");
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
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
		}
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
		titreBalise = "h1";
		boolean isbody = false;

		while ((ligne = br.readLine()) != null){
			if (isbody && ligne.contains("<") && !ligne.contains("<?")) {
				String balise = "p";
				//Si la balise ne tient pas en une ligne
				if (balise != null && !ligne.contains("</"+balise+">")) {
					String baliseText = ligne;
					ligne = br.readLine();
					while (ligne != null && !ligne.contains("</"+balise+">") && !ligne.contains("<?")){
						baliseText += ligne;
						ligne = br.readLine();
					}
					baliseText += (ligne != null) ? ligne : "";
					if (baliseText.contains("border-bottom:"))
						bw.write("<"+titreBalise+">"+getText(baliseText)+"</"+titreBalise+">");
					else 
						bw.write(baliseText);
				}
				//Si la balise tient en une ligne
				else if (balise != null && ligne.contains("</"+balise+">")) {
					if (ligne.contains("border-bottom:")) {
						bw.write("<"+titreBalise+">"+getText(ligne)+"</"+titreBalise+">\r\n");
					}else
						bw.write(ligne+"\r\n");
				}else
					bw.write(ligne);
			}
			else {
				if (ligne.contains("<body>"))
					isbody = true;
				else if (ligne.contains("</body>"))
					isbody = false;
				bw.write(ligne);
			}
		}
		
		br.close();
		bw.close();

		Principale.fileMove(tmp, f);
		tmp.delete();
		
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
