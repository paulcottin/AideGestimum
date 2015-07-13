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

import exceptions.FichierNonTrouve;
import interfaces.LancerAction;
import main.Principale;

public class ChoixFeuilleStyle extends Observable implements LancerAction{

	private ArrayList<File> cssFiles, htmlFiles;
	private String style, stylePath;
	private boolean running;

	public ChoixFeuilleStyle(ArrayList<File> files){
		this.cssFiles = new ArrayList<File>();
		this.htmlFiles = new ArrayList<File>();
		this.running = false;
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".css"))
				cssFiles.add(new File(Principale.FILE_BASE.toURI().relativize(file.toURI()).getPath()));
			else if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
		}

	}
	
	public void run() {
		if (cssFiles.size() > 0) {
			lancerActionAll();
		}else
			Principale.messageFin("Il faut définir des feuilles de styles CSS");
	}

	@Override
	public void lancerActionAll() {
		parametrer();
		if (style != null){
			applyStyle();
			Principale.messageFin("Feuilles de styles modifiées ("+style+")");
		}else
			Principale.messageFin("Il faut renseigner une feuille de style");
		running = false;
		update();
	}
	
	@Override
	public void fichiersSelectionnes(ArrayList<File> files) {
		lancerAction(files);
	}

	public void lancerAction(ArrayList<File> files){
		this.htmlFiles.clear();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
		}
	}
	
	@Override
	public void parametrer(){
		getStyle();
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

	private void applyStyle(){
		this.running = true;
		update();
		for (File file : htmlFiles) {
			try {
				applyStyleHelper(file);
			} catch (FileNotFoundException fnf){
				System.out.println("fichier non trouvé");
				new FichierNonTrouve(file.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void applyStyleHelper(File f) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(f));
		File tmp = new File(f.getAbsolutePath()+"_tmp");
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));

		String ligne = "";

		while ((ligne = br.readLine()) != null){
			if (ligne.contains("text/css") && ligne.contains("href")) {
				int debut = ligne.indexOf("href=\"")+"href=\"".length();
				int fin = debut + ligne.substring(debut).indexOf("\"");
				String avant = ligne.substring(0, debut);
				String apres = ligne.substring(fin, ligne.length());
				bw.write(avant+stylePath+apres+"\r\n");
			}else
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
		// Ne rien faire
	}
}
