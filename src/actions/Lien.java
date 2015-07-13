package actions;

import java.io.File;
import java.util.ArrayList;
import java.util.Observable;

import interfaces.LancerAction;

public class Lien extends Observable implements LancerAction {

	private boolean running;
	private ArrayList<File> htmlFiles;
	
	public Lien(ArrayList<File> files) {
		this.htmlFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
		}
		this.running = false;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void fichiersSelectionnes(ArrayList<File> files) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lancerActionAll() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void lancerAction(ArrayList<File> files) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void parametrer() {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		
	}

}
