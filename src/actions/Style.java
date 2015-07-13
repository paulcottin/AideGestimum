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
import main.Principale;
import utilitaires.StyleDialogue;
import utilitaires.StyleDialogueInfo;

public class Style extends Observable implements LancerAction {


	private String mot, style;
	private ArrayList<File> files, htmlFiles;
	private boolean running;
	private StyleDialogue sd;
	private StyleDialogueInfo sdInfo;

	public Style(ArrayList<File> files){
		this.files = files;
		this.running = false;
		this.htmlFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm")) {
				htmlFiles.add(file);
			}
		}

	}
	
	public void run() {
		running = true;
		update();
		lancerActionAll();
		running = false;
		update();
	}

	@Override
	public void lancerActionAll() {
		parametrer();
		if (sdInfo != null) {
			mot = sdInfo.getMot();
			style = sdInfo.getStyle();

			try {
				for (File f : htmlFiles) {
					appliquerStyle(f, mot, style);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

			Principale.messageFin("Changements effectués");
		}
		else 
			Principale.messageFin("Veuillez remplir correctement le formulaire");
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
		sd = new StyleDialogue(null, "Quel style appliquer", true, files);
		sdInfo = sd.showDialog();
	}

	private void appliquerStyle(File f, String mot, String style) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(f));
		File tmp = new File(f.getAbsolutePath()+"_tmp");
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
		String ligne = "";

		while((ligne = br.readLine()) != null){
			if (ligne.contains(mot)) {
				ligne = ligne.replace(mot, "<span class=\""+style+"\">"+mot+"</span>");
				bw.write(ligne+"\r\n");
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
		//Ne rien faire
	}


}
