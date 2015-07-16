package actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import interfaces.Action;

public class ChangerStyle extends Action {

	private String oldStyle, newStyle, oldStylePath, newStylePath;


	public ChangerStyle(ArrayList<File> files) {
		super(files);
		this.oldStyle = null;
		this.newStyle = null;
		intitule = "Changer d'un style à un autre";
		messageFin = "Changement de style effectué";
	}
	
	@Override
	public void parametrer(){
		correspondaceStyles();
	}
	
	@Override
	protected Document applyStyle(Document doc) throws IOException {	
			if (isBalise(oldStyle)) {
				Elements es = doc.select(oldStyle);
				if (isBalise(newStyle)) {
					for (Element element : es) {
						element.tagName(newStyle);
						for (Attribute a : element.attributes()) {
							element.removeAttr(a.getKey());
						}
						for (String s : element.classNames()) {
							element.removeClass(s);
						}
						if (isCleannable(element))
							element.text(element.text());
					}
				}
				else {
					
					for (Element element : es) {
						for (String s : element.classNames()) {
							element.removeClass(s);
						}
						element.addClass(newStyle);
						if (isCleannable(element))
							element.text(element.text());
					}
				}
			}
			else {
				Elements es = doc.select("."+oldStyle);
				if (isBalise(newStyle)) {
					for (Element element : es) {
						element.tagName(newStyle);
						for (Attribute a : element.attributes()) {
							element.removeAttr(a.getKey());
						}
						for (String s : element.classNames()) {
							element.removeClass(s);
						}
						if (isCleannable(element))
							element.text(element.text());
					}
				}
				else {
					
					for (Element element : es) {
						for (String s : element.classNames()) {
							element.removeClass(s);
						}
						element.addClass(newStyle);
						if (isCleannable(element))
							element.text(element.text());
					}
				}
			}
			
		return doc;
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
