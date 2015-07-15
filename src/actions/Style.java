package actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import interfaces.Action;

public class Style extends Action {

	private String mot, style;

	public Style(ArrayList<File> files){
		super(files);
		mot = "";
		style = "";
		intitule = "Application d'un style à un mot";
	}
	
	@Override
	public void parametrer(){
		getMot();
		getStyle(getCSSclasses(getCSSFile()));
	}
	
	@Override
	protected Document applyStyle(Document doc) throws IOException {
		System.out.println("mot : "+mot+", style : "+style);
		messageFin = "Le style \""+style+"\" a bien été appliqué sur le mot \""+mot+"\""; 
		String html = doc.html();
		html = html.replace(mot, "<span class=\""+style+"\">"+mot+"</span>");
		doc = Jsoup.parse(html);
		return doc;
	}
	
	private void getMot(){
		mot = JOptionPane.showInputDialog(null, "<html>Quel mot ?<br/>(Attention à la casse + pas d'accent)</html>", 
				"Paramétrage", JOptionPane.QUESTION_MESSAGE);
	}
	
	private String getCSSFile(){
		String cssFilePath = "";
		String[] cssFiles = new String[this.cssFiles.size()];
		for (int i = 0; i < this.cssFiles.size(); i++) {
			cssFiles[i] = this.cssFiles.get(i).getName();
		}

		cssFilePath =	(String) JOptionPane.showInputDialog(null, 
				"Choisir la feuille de style",
				"Paramétrage",
				JOptionPane.QUESTION_MESSAGE,
				null,
				cssFiles, cssFiles[0]);
		
		for (File file: this.cssFiles) {
			if (file.getName().equals(cssFilePath))
				cssFilePath = file.getAbsolutePath();
		}
		
		return cssFilePath;
	}
	
	private ArrayList<String> getCSSclasses(String cssFilePath){
		ArrayList<String> reponse = new ArrayList<String>();


		File file = new File(cssFilePath);

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			String ligne = "";
			while((ligne = br.readLine()) != null){
				if (ligne.startsWith("span.")) {
					int deb = ligne.indexOf("span.") + "span.".length();
					int fin = ligne.indexOf("{");
					reponse.add(ligne.substring(deb, fin));
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();

		}
		return reponse;
	}


	private void getStyle(ArrayList<String> styles){
		String[] s = new String[styles.size()];
		for (int i = 0; i < s.length; i++) {
			s[i] = styles.get(i);
		}
		
		style = (String)JOptionPane.showInputDialog(null, 
		      "Quel style appliquer",
		      "Paramétrage",
		      JOptionPane.QUESTION_MESSAGE,
		      null,
		      s,
		      s[2]);
	}
}
