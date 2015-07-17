package main;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JOptionPane;

import interfaces.Action;
import interfaces.LongTask;
import interfaces.NeedSelectionFichiers;
import vues.ChoixFichiers;
import vues.ProgressBar;
import vues.ScriptChooser;

public class Script extends Observable implements NeedSelectionFichiers, LongTask{

	private ArrayList<Action> actions;
	private ArrayList<File> files, ppFiles, cssFiles;
	private ScriptChooser scriptChooser;
	private boolean running;
	private Principale principale;

	public Script(ArrayList<File> files, Principale principale) {
		this.actions = new ArrayList<Action>();
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
		this.principale = principale;
	}

	public void runActions(){
		Thread th;
		for (Action lancerAction : actions) {
			lancerAction.fichiersSelectionnes(files);
//			th = new Thread(lancerAction);
//			th.start();
//			new ProgressBar(lancerAction);
			lancerAction.run();		
		}
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

	@SuppressWarnings("unchecked")
	public void choisirActionHelper(){
		scriptChooser.dispose();
		ArrayList<File> fileEtCssEtHtt = new ArrayList<File>();
		fileEtCssEtHtt.addAll(files);
		fileEtCssEtHtt.addAll(cssFiles);
		fileEtCssEtHtt.addAll(ppFiles);
		for (String string : scriptChooser.getActionsChoisies()) {
			for (Action a : principale.getScripts()) {
				if (string.equals(a.getIntitule())) {
					Class<Action> action;
					try {
						action = (Class<Action>) Class.forName(a.getClass().getName());
						Constructor<Action> constructeur = action.getConstructor(new Class[] {
								Class.forName("java.util.ArrayList")
						});
						this.actions.add(constructeur.newInstance(fileEtCssEtHtt));
					} catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				}
			}
		}
		runActions();
	}

	public ArrayList<Action> getActions() {
		return actions;
	}

	public void setActions(ArrayList<Action> actions) {
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

	public Principale getPrincipale() {
		return principale;
	}

	public void setPrincipale(Principale principale) {
		this.principale = principale;
	}

}
