package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import actions.AssociationAuto;
import actions.ChangerStyle;
import actions.ChoixFeuilleStyle;
import actions.ChoixPagePrincipale;
import actions.ColorationPuces;
import actions.Copyright;
import actions.CreationPuce;
import actions.Lien;
import actions.NettoyagePagePrincipale;
import actions.NettoyageTitre;
import actions.NoPP;
import actions.Style;
import actions.SupprStyleTitre;
import actions.SupprimerTitre;
import actions.Titre;
import interfaces.Action;
import vues.Fenetre;

public class Principale extends Observable {

	public static File FILE_BASE;

	private Script script;
	private ArrayList<Action> scripts;

	File topics;
	ArrayList<File> files;


	public Principale(){
		files = new ArrayList<File>();
		topics = new File(getTopicsPath());

//		topics = new File("C:\\Users\\paul-cot\\Desktop\\GestimumCompta - Copie");
//		topics = new File("\\\\SRVSI\\6-Developpement\\Versions\\ERP\\5\\Test\\Aide\\2015\\Gestimum gesco - Copie");

		listerRepertoire(topics);

		script = new Script(files, this);
		scripts = new ArrayList<Action>();
		initActions();

		setChanged();
		notifyObservers();
	}

	private void initActions(){
		scripts.add(new Style(files));
		scripts.add(new ChoixFeuilleStyle(files));
		scripts.add(new ChoixPagePrincipale(files));
		scripts.add(new AssociationAuto(files));
		scripts.add(new ColorationPuces(files));
		scripts.add(new ChangerStyle(files));
		scripts.add(new Titre(files));
		scripts.add(new CreationPuce(files));
		scripts.add(new Lien(files));
		scripts.add(new SupprimerTitre(files));
		scripts.add(new SupprStyleTitre(files));
		scripts.add(new NettoyagePagePrincipale(files));
		scripts.add(new NettoyageTitre(files));
		scripts.add(new Copyright(files));
		scripts.add(new NoPP(files));
	}

	private String getTopicsPath(){
		JOptionPane.showMessageDialog(null, "<html><h3>Fermer RoboHelp avant toute utilisation de ce programme</h3>"
				+ "Veuillez donner le chemin vers <span style=\"color:red;\">le dossier</span> du projet<br/>Penser à mettre à jour Java"
				+ "(Version 1.8 ou supérieure)</html>");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(getCurrentDir());
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			File f = fileChooser.getSelectedFile();
			return f.getAbsolutePath();
		}
		else
			return null;
	}

	private void listerRepertoire(File topics){ 
		File[] listefichiers; 

		listefichiers=topics.listFiles();
		for(int i=0;i<listefichiers.length;i++){ 
			//Spécifie un fichier de base pour traiter les autres liens en relatif
			if (listefichiers[i].getAbsolutePath().endsWith(".xpj"))
				FILE_BASE = listefichiers[i];

			if (listefichiers[i].isDirectory() && !listefichiers[i].getAbsolutePath().contains("!"))
				listerRepertoire(listefichiers[i]);
			else if (!listefichiers[i].isDirectory())
				files.add(listefichiers[i]);
		}
	}

	public void exportCSV() throws IOException{
		File f;
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setApproveButtonText("Enregistrer");
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			f = fileChooser.getSelectedFile();
		}else
			f = null;

		if (f != null) {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));

			for (File file : files) {
				if (file.getAbsolutePath().endsWith(".htm")) {
					bw.write(file.getAbsolutePath()+"\r\n");
				}
			}
			bw.close();

			messageFin("Export des données ok !");
		}else
			messageFin("Veuillez donner un nom valide");

	}

	private File getCurrentDir(){
		String tmp = Principale.class.getResource("Principale.class").toString();

		tmp = tmp.substring("file:/".length()+4, (tmp.length()-"/bin/main/Principale.class".length()));
		String[] tab = tmp.split("/");
		tmp = tmp.substring(0, tmp.length()-tab[tab.length-1].length());
		return new File(tmp);
	}

	public static void fileMove(File from, File to) throws FileSystemException{
		Path pathProject = FileSystems.getDefault().getPath(to.getAbsolutePath());
		Path pathTmp = FileSystems.getDefault().getPath(from.getAbsolutePath());
		try {
			Files.move(pathTmp, pathProject, StandardCopyOption.REPLACE_EXISTING);
		} catch (FileSystemException fse){
			throw fse;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void fileCopy(File from, File to) throws FileSystemException{
		Path pathProject = FileSystems.getDefault().getPath(to.getAbsolutePath());
		Path pathTmp = FileSystems.getDefault().getPath(from.getAbsolutePath());
		try {
			Files.copy(pathTmp, pathProject, StandardCopyOption.REPLACE_EXISTING);
		} catch (FileSystemException fse){
			throw fse;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void messageFin(String message){
		JOptionPane.showMessageDialog(null, "<html>"+message+"</html>");
	}

	public static void main(String[] args){
		Principale p = new Principale();
		@SuppressWarnings("unused")
		Fenetre fen = new Fenetre(p);
	}

	public ArrayList<File> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}

	public Script getScript() {
		return script;
	}

	public void setScript(Script script) {
		this.script = script;
	}

	public ArrayList<Action> getScripts() {
		return scripts;
	}

	public void setScripts(ArrayList<Action> scripts) {
		this.scripts = scripts;
	}
}
