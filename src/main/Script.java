package main;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JOptionPane;

import actions.ChangerStyle;
import actions.ChoixFeuilleStyle;
import actions.ChoixPagePrincipale;
import actions.ColorationPuces;
import actions.Style;
import actions.SupprimerBalise;
import interfaces.LancerAction;
import interfaces.NeedSelectionFichiers;
import vues.ChoixFichiers;
import vues.ScriptChooser;

public class Script extends Observable implements NeedSelectionFichiers{

	private ArrayList<LancerAction> actions;
	private ArrayList<File> files, ppFiles, cssFiles;
	private ScriptChooser scriptChooser;
	private boolean running;

	public Script(ArrayList<File> files) {
		this.actions = new ArrayList<LancerAction>();
		this.files = new ArrayList<File>();
		this.cssFiles = new ArrayList<File>();
		this.ppFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htt"))
				ppFiles.add(file);
			else if (file.getAbsolutePath().endsWith(".css"));
				cssFiles.add(file);
			this.files.add(file);
		}
		this.running = false;
	}

	public void runActions(){
		for (LancerAction lancerAction : actions) {
			lancerAction.lancerAction(files);
			lancerAction.lancerActionAll();
		}
		Principale.messageFin("Script fini");
	}
	
	@Override
	public void run(){
		//Ne fait rien
	}

	@Override
	public void fichiersSelectionnes(ArrayList<File> files) {
		this.files.clear();
		this.files.addAll(files);
		choisirActions();
	}

	public boolean choisirFichiers(){
		String[] choixFichiers = {"Tous", "Sélection"};
		String choix = (String)JOptionPane.showInputDialog(null, 
				"Sur quels fichiers voulez-vous travailler",
				"Choix des fichiers",
				JOptionPane.QUESTION_MESSAGE,
				null,
				choixFichiers,
				choixFichiers[0]);

		if (choix != null) {
			if (choix.equals("Tous")) {
				choisirActions();
			}
			else if (choix.equals("Sélection")){
				new ChoixFichiers(files, this);
			}
			return true;
		}else
			return false;
	}

	public void choisirActions(){
		scriptChooser = new ScriptChooser(this);
	}

	public void choisirActionHelper(){
		scriptChooser.dispose();
		ArrayList<File> fileEtCssEtHtt = new ArrayList<File>();
		fileEtCssEtHtt.addAll(files);
		fileEtCssEtHtt.addAll(cssFiles);
		fileEtCssEtHtt.addAll(ppFiles);
		for (String string : scriptChooser.getActionsChoisies()) {
			switch (string) {
			case "Choix page principale":
				actions.add(new ChoixPagePrincipale(fileEtCssEtHtt));
				break;
			case "Changement d'une classe CSS":
				actions.add(new ChangerStyle(fileEtCssEtHtt));
				break;
			case "Choix d'une feuille de style":
				actions.add(new ChoixFeuilleStyle(fileEtCssEtHtt));
				break;
			case "Coloration des puces":
				actions.add(new ColorationPuces(fileEtCssEtHtt));
				break;
			case "Application d'un style à un mot":
				actions.add(new Style(fileEtCssEtHtt));
				break;
			case "Supprimer une balise":
				actions.add(new SupprimerBalise(fileEtCssEtHtt));
				break;
			default:
				System.err.println("Erreur Script, choix inconnu");
				break;
			}
		}
		runActions();
	}

	public ArrayList<LancerAction> getActions() {
		return actions;
	}

	public void setActions(ArrayList<LancerAction> actions) {
		this.actions = actions;
	}

	public ArrayList<File> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<File> files) {
		this.files = files;
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
