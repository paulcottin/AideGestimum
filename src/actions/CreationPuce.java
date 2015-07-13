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

public class CreationPuce extends Observable implements LancerAction {

	private ArrayList<File> htmlFiles;
	private boolean running;

	public CreationPuce(ArrayList<File> files) {
		htmlFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
		}
		running = false;
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
				creaPuce(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Principale.messageFin("création puce est finie");
	}

	@Override
	public void lancerAction(ArrayList<File> files) {
		htmlFiles.clear();
		htmlFiles.addAll(files);
	}

	@Override
	public void parametrer() {

	}

	private void creaPuce(File file) throws IOException{
		File tmp = new File("tmp");
		BufferedReader br = new BufferedReader(new FileReader(file));
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));

		String ligne = "";
		String balise = "p";
		while ((ligne = br.readLine()) != null){
			//Si la balise ne tient pas en une ligne
			if (balise != null && !ligne.contains("</"+balise+">")) {
				String baliseText = ligne;
				ligne = br.readLine();
				while (ligne != null && !ligne.contains("</"+balise+">") && !ligne.contains("<?")){
					baliseText += ligne;
					ligne = br.readLine();
				}
				baliseText += (ligne != null) ? ligne : "";

				if (baliseText.contains(">- ")) {
					bw.write("<ul>\r\n");
					bw.write("<li>"+baliseText.substring(baliseText.indexOf(">- ")+">- ".length())+"</li>\r\n");
				}	else 
					bw.write(baliseText+"\r\n");
			}
			//Si la balise tient en une ligne
			else if (ligne.contains(">- ")) {
				bw.write("<ul>\r\n");
				bw.write("<li>"+ligne.substring(ligne.indexOf(">- ")+">- ".length())+"</li>\r\n");
				while ((ligne = br.readLine()).contains(">- ")) {
					bw.write("<li>"+ligne.substring(ligne.indexOf(">- ")+">- ".length())+"</li>\r\n");
				}
				bw.write("</ul>\r\n");
			}else
				bw.write(ligne+"\r\n");
		}
		br.close();
		bw.close();

		Principale.fileMove(tmp, file);
		tmp.delete();

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
		// Ne rien faire
	}

	private void update(){
		setChanged();
		notifyObservers();
	}

}
