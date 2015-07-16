package actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JOptionPane;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import exceptions.FichierNonTrouve;
import interfaces.Action;
import interfaces.LancerAction;
import main.Principale;

public class ChoixFeuilleStyle extends Action {

	private String style, stylePath;
	public ChoixFeuilleStyle(ArrayList<File> files){
		super(files);
		intitule = "Choix d'une feuille de style";
		messageFin = "Feuille de style appliquée";
	}



	private void getStyle(){
		String[] styles = new String[cssFiles.size()];
		for (int i = 0; i < cssFiles.size(); i++) {
			styles[i] = cssFiles.get(i).getName();
		}

		style =	(String) JOptionPane.showInputDialog(null, 
				"Quel style voulez-vous appliquer",
				"Modification générale du style",
				JOptionPane.QUESTION_MESSAGE,
				null,
				styles, styles[0]);
		ArrayList<String> tmp = new ArrayList<String>();
		for (String string : styles) {
			tmp.add(string);
		}
		stylePath = cssFiles.get(tmp.indexOf(style)).getPath();
	}

	//	private void applyStyleHelper(File f) throws IOException{
	//		BufferedReader br = new BufferedReader(new FileReader(f));
	//		File tmp = new File(f.getAbsolutePath()+"_tmp");
	//		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
	//
	//		String ligne = "";
	//
	//		while ((ligne = br.readLine()) != null){
	//			if (ligne.contains("text/css") && ligne.contains("href")) {
	//				int debut = ligne.indexOf("href=\"")+"href=\"".length();
	//				int fin = debut + ligne.substring(debut).indexOf("\"");
	//				String avant = ligne.substring(0, debut);
	//				String apres = ligne.substring(fin, ligne.length());
	//				bw.write(avant+stylePath+apres+"\r\n");
	//			}else
	//				bw.write(ligne+"\r\n");
	//		}
	//
	//		br.close();
	//		bw.close();
	//
	//		Principale.fileMove(tmp, f);
	//		tmp.delete();
	//	}

	@Override
	protected Document applyStyle(Document doc) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("coucou");

		Elements style = doc.select("link[rel=StyleSheet]");
//		int n = style.size();
//		System.out.println("n:"+n);
//		if (n > 1) {
//			for (int i = 0; i < n-1; i++) {
//				style.remove(i);
//			}
//			style.attr("href", stylePath);
//
//		}else
			for (Element element : style) {
				element.attr("href", stylePath);
			}
		return doc;
	}



	@Override
	public void parametrer() {
		// TODO Auto-generated method stub
		getStyle();
	}
}
