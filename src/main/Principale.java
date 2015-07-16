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
import actions.CreationPuce;
import actions.Lien;
import actions.Style;
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
		//		topics = new File(getTopicsPath());

		//		topics = new File("C:\\Users\\paul-cot\\Desktop\\GestimumCompta - Copie");
		topics = new File("\\\\SRVSI\\6-Developpement\\Versions\\ERP\\5\\Test\\Aide\\2015\\Gestimum gesco - Copie");

		listerRepertoire(topics);

		script = new Script(files);
		scripts = new ArrayList<Action>();
		initActions();

		setChanged();
		notifyObservers();
	}

	private void initActions(){
		scripts.add(new Style(files));
		scripts.add(new ChoixFeuilleStyle(files));
		scripts.add(new ChoixPagePrincipale(files));
		//		scripts.add(new SupprimerBalise(files));
		scripts.add(new AssociationAuto(files));
		scripts.add(new ColorationPuces(files));
		scripts.add(new ChangerStyle(files));
		scripts.add(new Titre(files));
		scripts.add(new CreationPuce(files));
		scripts.add(new Lien(files));
		scripts.add(new SupprimerTitre(files));
	}

	private String getTopicsPath(){
		JOptionPane.showMessageDialog(null, "<html><h3>Fermer RoboHelp avant toute utilisation de ce programme</h3>"
				+ "Veuillez donner le chemin vers <span style=\"color:red;\">le dossier</span> du projet<br/>Penser � mettre � jour Java"
				+ "(Version 1.8 ou sup�rieure)</html>");
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
			//Sp�cifie un fichier de base pour traiter les autres liens en relatif
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

			messageFin("Export des donn�es ok !");
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

	//	public void modifNomFichiers(){
	//		//Choix du dossier
	//		File f;
	//		JOptionPane.showMessageDialog(null, "Veuillez indiquer le dossier o� les noms de fichiers sont � normaliser");
	//		JFileChooser fileChooser = new JFileChooser();
	//		fileChooser.setCurrentDirectory(getCurrentDir());
	//		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	//		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
	//			f = fileChooser.getSelectedFile();
	//		else
	//			return;
	//		File d = new File(f.getParentFile().getAbsolutePath()+"\\FichiersNormalis�s");
	//		d.mkdir();
	//		try {
	//			modifNomFichiersHelper(f, d);
	//		} catch (IOException e) {
	//			e.printStackTrace();
	//		}
	//
	//	}
	//
	//	private void modifNomFichiersHelper(File directory, File outPut) throws IOException{
	//		//list des fichiers
	//		File[] files = directory.listFiles();
	//		
	//		for (int i = 0; i < files.length; i++) {
	//			String name = files[i].getName();
	//			//Modification du nom du fichier
	//			if (files[i].getAbsolutePath().endsWith(".htm") || files[i].isDirectory()) {
	//				//Suppression du pr�fixe
	//				name = name.substring(12);
	//				//Changement de _ en " "
	//				name = name.replace("_", " ");
	//			}
	//			if ((files[i]).isDirectory()){
	//				File d = new File(outPut.getAbsolutePath()+"\\"+name);
	//				d.mkdir();
	//				modifNomFichiersHelper(files[i], new File(outPut.getAbsolutePath()+"\\"+name));
	//			}
	//			else if (!name.endsWith("/") && name != ""){
	//				//Mise � jour des liens et recopie du fichier
	//				File tmp = new File(outPut.getAbsolutePath()+"\\"+name);
	//
	//				try {
	//					BufferedReader br = new BufferedReader(new FileReader(files[i]));
	//					BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
	//					String ligne = "";
	//					
	//					while ((ligne = br.readLine()) != null){
	//						if (ligne.contains(files[i].getName()) && !ligne.contains(files[i].getAbsolutePath()))
	//							bw.write(ligne.replace(files[i].getName(), name));
	//						else if (!ligne.contains(files[i].getName()) && ligne.contains(files[i].getAbsolutePath()))
	//							bw.write(ligne.replace(files[i].getAbsolutePath(), files[i].getParentFile().getAbsolutePath()+"\\"+name));
	//						else
	//							bw.write(ligne);
	//					}
	//					bw.close();
	//					br.close();
	//					fileMove(tmp, files[i]);
	//				} catch (FileSystemException e) {
	//					e.printStackTrace();
	//				}
	//			}
	//		}
	//	}

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
		JOptionPane.showMessageDialog(null, message);
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
