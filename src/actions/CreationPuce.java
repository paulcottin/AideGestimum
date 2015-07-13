package actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

import interfaces.LancerAction;

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
