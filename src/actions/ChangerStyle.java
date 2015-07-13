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

import javax.swing.JOptionPane;

import interfaces.LancerAction;
import main.Principale;

public class ChangerStyle extends Observable implements LancerAction {

	private ArrayList<File> htmlFiles, cssFiles;
	private boolean running;
	private String oldStyle, newStyle, oldStylePath, newStylePath;
	private ArrayList<String> baliseASauver;


	public ChangerStyle(ArrayList<File> files) {
		this.htmlFiles = new ArrayList<File>();
		this.cssFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
			else if (file.getAbsolutePath().endsWith(".css"))
				cssFiles.add(file);
		}
		this.running = false;
		this.oldStyle = null;
		this.newStyle = null;
		this.baliseASauver = new ArrayList<String>();
		initBaliseASauver();
	}

	@Override
	public void run() {
		if (cssFiles.size() > 0) {
			lancerActionAll();
		}else
			Principale.messageFin("Il faut définir des feuilles de styles CSS");
	}

	@Override
	public void lancerActionAll() {
		parametrer();
		if (oldStyle != null && newStyle != null) {
			running = true;
			update();
			for (File file : htmlFiles) {
				try {
					if (isBalise(newStyle))
						changeBalise(file);
					else
						changeStyle(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			running = false;
			update();
		}else
			Principale.messageFin("Il faut renseigner les champs de nouveau ET d'ancien style");
		
		Principale.messageFin("Les styles ont bien été modifiés");
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
	public void parametrer(){
		correspondaceStyles();
	}

	/**
	 * Si le nouveau style est une classe CSS définie par l'utilisateur, on remplace la ligne de texte avec toutes ses balises par 
	 * <p class="la nouvelle classe"> le texte </p> de façon à nettoyer un peu le document
	 * @param f : le fichier sur lequel on applique le traitement
	 * @throws IOException
	 */
	private void changeStyle(File f) throws IOException{
		File tmp = new File("temp");
		BufferedReader br = new BufferedReader(new FileReader(f));
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));

		String ligne = "";
		String baliseP = "";
		while ((ligne = br.readLine()) != null){
			if (ligne.contains("class=\""+oldStyle+"\"")) {
				if (ligne.contains("</p>")) 
					bw.write("<p class=\""+newStyle+"\">"+getText(ligne)+"</p>");
				else {
					baliseP = ligne;
					while (!(ligne = br.readLine()).contains("</p>")){
						baliseP += ligne;
					}
					baliseP += ligne;
					bw.write("<p class=\""+newStyle+"\">"+getText(baliseP)+"</p>");
				}
			}
			else
				bw.write(ligne+"\r\n");
		}

		br.close();
		bw.close();

		Principale.fileMove(tmp, f);
		tmp.delete();
	}
	
	/**
	 * Si le nouveau style se traduit simplement par une balise (p, h1, h2,...), on remplace la ligne avec toutes ses balises par
	 * <ma balise> mon texte </ma balise> pour nettoyer un peu le document
	 * @param f : Fichier auquel on applique le traitement
	 * @throws IOException
	 */
	private void changeBalise(File f) throws IOException {
		String balise = newStyle;
		File tmp = new File("temp");
		BufferedReader br = new BufferedReader(new FileReader(f));
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));

		String ligne = "";
		String baliseP = "";
		while ((ligne = br.readLine()) != null){
			if (ligne.contains("class=\""+oldStyle+"\"") || (oldStyle.equals("p") && ligne.contains("<p ") && !isSafeBaliseInLine(ligne)) || 
					(oldStyle.equals("p") && ligne.contains("<p><span ") && !isSafeBaliseInLine(ligne))) {
				
				if (ligne.contains("</p>")) 
					bw.write("<"+balise+">"+getText(ligne)+"</"+balise+">");
				else {
					baliseP = ligne;
					while (!(ligne = br.readLine()).contains("</p>")){
						baliseP += ligne;
					}
					baliseP += ligne;
					System.out.println("texte : "+getText(baliseP));
					bw.write("<"+balise+">"+getText(baliseP)+"</"+balise+">");
				}
			}
			else
				bw.write(ligne+"\r\n");
		}

		br.close();
		bw.close();

		Principale.fileMove(tmp, f);
		tmp.delete();
	}

	/**
	 * On demande à l'utilisateur quelles feuilles de styles il choisit et quelle classe dans ces feuilles
	 */
	private void correspondaceStyles(){
		String cssFilePath1 = null, cssFilePath2 = null;
		String[] cssFiles = new String[this.cssFiles.size()];
		for (int i = 0; i < this.cssFiles.size(); i++) {
			cssFiles[i] = this.cssFiles.get(i).getName();
		}
		
		cssFilePath1 =	(String) JOptionPane.showInputDialog(null, 
				"Choisir la feuille de style de départ",
				"Changement de style",
				JOptionPane.QUESTION_MESSAGE,
				null,
				cssFiles, cssFiles[0]);
		
		cssFilePath2 =	(String) JOptionPane.showInputDialog(null, 
				"Choisir la feuille de style d'arrivée",
				"Changement de style",
				JOptionPane.QUESTION_MESSAGE,
				null,
				cssFiles, cssFiles[0]);
		
		ArrayList<String> tmp = new ArrayList<String>();
		for (String string : cssFiles) {
			tmp.add(string);
		}
		cssFilePath1 = this.cssFiles.get(tmp.indexOf(cssFilePath1)).getPath();
		cssFilePath2 = this.cssFiles.get(tmp.indexOf(cssFilePath2)).getPath();
		
		String[] styles = afficheCSSClasses(getCSSclasses(new File(cssFilePath1)));
		oldStyle =	(String) JOptionPane.showInputDialog(null, 
				"Style de départ",
				"Changement de style",
				JOptionPane.QUESTION_MESSAGE,
				null,
				styles, styles[0]);
		
		oldStyle = getCSSBalise(oldStyle);

		styles = afficheCSSClasses(getCSSclasses(new File(cssFilePath2)));
		newStyle =	(String) JOptionPane.showInputDialog(null, 
				"Style d'arrivée",
				"Changement de style",
				JOptionPane.QUESTION_MESSAGE,
				null,
				styles, styles[0]);
		
		newStyle = getCSSBalise(newStyle);
	}
	
	/**
	 * Récupère le texte brut présent dans une ligne du document
	 * Mise à part des balises d'image et de lien
	 * @param ligne
	 * @return
	 */
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
	
	/**
	 * Affiche de manière compréhensible pour l'utilisateur certaines classes CSS (p => Normal, H1 => Titre 1, ...)
	 * @param classes
	 * @return
	 */
	private String[] afficheCSSClasses(ArrayList<String> classes){
		String[] styles = new String[classes.size()];
		for (int i = 0; i < classes.size(); i++) {
			if (classes.get(i).equals("p"))
				styles[i] = "Normal";
			else if (classes.get(i).matches("(H|h)[0-9]"))
				styles[i] = "Titre "+classes.get(i).substring(1);
			else
				styles[i] = classes.get(i);
		}
		return styles;
	}
	
	/**
	 * D'un titre affiché donne le nom de la balise ou de la classe CSS
	 * @param classeAffichee
	 * @return
	 */
	private String getCSSBalise(String classeAffichee){
		if (classeAffichee.equals("Normal"))
			return "p";
		else if (classeAffichee.matches("Titre [0-9]"))
			return "H"+classeAffichee.substring("Titre ".length());
		else
			return classeAffichee;
	}

	/**
	 * Récupère les classes/balises d'un document CSS
	 * @param cssFile
	 * @return
	 */
	private ArrayList<String> getCSSclasses(File cssFile){
		ArrayList<String> reponse = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(cssFile));

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
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reponse;
	}

	/**
	 * 
	 * @param style
	 * @return vrai si le style est une balise, faux si c'est une classe CSS définie par l'utilisateur
	 */
	private boolean isBalise(String style){
		if (style.equals("p"))
			return true;
		else return false;
	}
	
	private void initBaliseASauver(){
		baliseASauver.add("<img");
		baliseASauver.add("<a");
		baliseASauver.add("<ul");
		baliseASauver.add("<li");
		baliseASauver.add("h1");
		baliseASauver.add("H1");
		baliseASauver.add("h2");
		baliseASauver.add("H2");
		baliseASauver.add("h3");
		baliseASauver.add("H3");
		baliseASauver.add("h4");
		baliseASauver.add("H4");
	}
	
	private boolean isSafeBaliseInLine(String ligne){
		for (String string : baliseASauver) {
			if (ligne.contains(string))
				return true;
		}
		return false;
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
		// Ne rien faire
	}

	public String getOldStyle() {
		return oldStyle;
	}

	public void setOldStyle(String oldStyle) {
		this.oldStyle = oldStyle;
	}

	public String getNewStyle() {
		return newStyle;
	}

	public void setNewStyle(String newStyle) {
		this.newStyle = newStyle;
	}

	public String getOldStylePath() {
		return oldStylePath;
	}

	public void setOldStylePath(String oldStylePath) {
		this.oldStylePath = oldStylePath;
	}

	public String getNewStylePath() {
		return newStylePath;
	}

	public void setNewStylePath(String newStylePath) {
		this.newStylePath = newStylePath;
	}

}
