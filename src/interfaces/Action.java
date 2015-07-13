package interfaces;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

import main.Principale;

public abstract class Action extends Observable implements LancerAction{

	protected ArrayList<File> htmlFiles, cssFiles, ppfiles;
	protected boolean running;
	protected String messageFin;
	protected String intitule;
	
	public Action(ArrayList<File> files) {
		htmlFiles = new ArrayList<File>();
		cssFiles = new ArrayList<File>();
		ppfiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
			else if (file.getAbsolutePath().endsWith(".css"))
				cssFiles.add(file);
			else if (file.getAbsolutePath().endsWith(".htt"))
				ppfiles.add(file);
		}
		
		this.running = false;
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
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
			else if (file.getAbsolutePath().endsWith(".css"))
				cssFiles.add(file);
			else if (file.getAbsolutePath().endsWith(".htt"))
				ppfiles.add(file);
		}
	}


	@Override
	public void lancerActionAll() {
		parametrer();
		running = true;
		update();
		for (File file : htmlFiles) {
			try {
				applyStyleHelper(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		Principale.messageFin(messageFin);
	}
	
	private void applyStyleHelper(File file) throws IOException{
		File tmp = new File("tmp");
		BufferedReader br = new BufferedReader(new FileReader(file));
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
		String ligne = "";
		
		while ((ligne = br.readLine()) != null){
			applyStyle(br, bw, ligne);
		}
		
		br.close();
		bw.close();

		Principale.fileMove(tmp, file);
		tmp.delete();
	}
	
	protected abstract void applyStyle(BufferedReader br, BufferedWriter bw, String ligne) throws IOException;


	@Override
	public void lancerAction(ArrayList<File> files) {
		htmlFiles.clear();
		
	}


	@Override
	public void parametrer() {
		
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
		//Ne rien faire
	}
	
	protected void update(){
		setChanged();
		notifyObservers();
	}


	public String getIntitule() {
		return intitule;
	}


	public void setIntitule(String intitule) {
		this.intitule = intitule;
	}
}
