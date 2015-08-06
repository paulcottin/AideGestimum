package utilitaires;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JFileChooser;

import interfaces.LongTask;
import main.Principale;
import vues.ProgressBar;

public class Utils extends Observable implements LongTask {
	
	private boolean running;
	private String fichierTraitement;
	
	public Utils() {
		running = false;
		fichierTraitement = "";
	}
	
	public void generateFileSize(ArrayList<File> files) {
		String s = "";
		for (File file : files)
			s += file.getAbsolutePath()+";"+(file.length()/1024)+"K\r\n";
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(chooseSaveFile()));
			bw.write(s);
			bw.close();
		} catch (IOException e) {
			Principale.messageFin("Erreur pendant l'exportation");
			e.printStackTrace();
		}
		
		Principale.messageFin("Fichier exporté !");
	}
	
	public File chooseSaveFile(){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setApproveButtonText("Enregistrer");
		fileChooser.setDialogTitle("Sauvegarde du fichier");
		
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}else
			return null;
	}
	
	public File chooseFolder(){
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(getCurrentDir());
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			return new File(f.getAbsolutePath());
		}
		else
			return null;
	}
	
	public void checkNoHelpWindows() {
		new Thread(){
			public void run() {
				checkNoHelpWindowsHelper();
			}
		}.start();
		new ProgressBar(this);
	}
	
	private void checkNoHelpWindowsHelper(){
		File folder = chooseFolder();
		running = true;
		ArrayList<File> list = listerRepertoire(folder);
		String msg = "Ce sont les pages : <br/><ul>";
		int cpt = 0;
		
		for (File file : list) {
			fichierTraitement = file.getName();
			setChanged();notifyObservers();
			//Si c'est une fenêtre
			if (file.getAbsolutePath().endsWith(".pas") && sameFileName(list, file.getName().substring(0, file.getName().length()-3)+"dfm")) {
				boolean find = false;
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					String ligne = "";
					while ((ligne = br.readLine()) != null) {
						//Si on trouve la mention "HelpContext :=" alors il y a une page d'aide fixée, sinon non.
						if (ligne.contains("HelpContext :="))
							find = true;
					}
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (!find){
					msg += "<li>"+file.getPath()+"</li>";
					cpt++;
				}
			}
		}
		running = false;
		setChanged();notifyObservers();
		msg += "</ul>";
		msg = "Au total, "+cpt+" fenêtres n'ont pas d'id d'aide<br/>" + msg;
		
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(chooseSaveFile()));
			bw.write(msg);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Principale.messageFin("Génération du fichier résultat terminée");
	}
	
	public boolean sameFileName(ArrayList<File> fileList, String path) {
		for (File file : fileList) 
			if (file.getName().equals(path)) 
				return true;
		return false;
	}
	
	public File getCurrentDir(){
		String tmp = System.getProperty("user.dir");
		return new File(tmp);
	}
	
	public ArrayList<File> listerRepertoire(File topics){ 
		ArrayList<File> files = new ArrayList<File>();
		File[] listefichiers; 

		listefichiers=topics.listFiles();
		for(int i=0;i<listefichiers.length;i++){ 
			//Spécifie un fichier de base pour traiter les autres liens en relatif
			if (listefichiers[i].getAbsolutePath().endsWith(".xpj"))
				Principale.FILE_BASE = listefichiers[i];

			if (listefichiers[i].isDirectory() && !listefichiers[i].getAbsolutePath().contains("!"))
				files.addAll(listerRepertoire(listefichiers[i]));
			else if (!listefichiers[i].isDirectory())
				files.add(listefichiers[i]);
		}
		return files;
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
	public void onProgressBarDispose() {
		//Ne rien faire
	}

	@Override
	public String getFichierTraitement() {
		return fichierTraitement;
	}

	@Override
	public String getTitre() {
		return "Recherche des fenêtres n'ayant pas d'id d'aide";
	}

}
