package interfaces;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import main.Principale;

public abstract class Action extends Observable implements LancerAction{

	protected ArrayList<File> htmlFiles, cssFiles, ppFiles;
	protected boolean running;
	protected String messageFin;
	protected String intitule;
	private ArrayList<String> baliseASauver;
	
	public Action(ArrayList<File> files) {
		htmlFiles = new ArrayList<File>();
		cssFiles = new ArrayList<File>();
		ppFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
			else if (file.getAbsolutePath().endsWith(".css"))
				cssFiles.add(file);
			else if (file.getAbsolutePath().endsWith(".htt"))
				ppFiles.add(file);
		}
		
		this.running = false;
		baliseASauver = new ArrayList<String>();
		initBaliseASauver();
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
				ppFiles.add(file);
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
		String ligne = "", txt = "";
		
		while ((ligne = br.readLine()) != null){
			txt += ligne+"\r\n";
		}
		
		Document doc = Jsoup.parse(txt);
		
		doc = applyStyle(doc);
		
		String html = doc.html();

		html = html.replace("<!--?", "<?");
		html = html.replace("?-->", "?>");
		
		bw.write(html);
		
		br.close();
		bw.close();

		Principale.fileMove(tmp, file);
		tmp.delete();
	}
	
	protected abstract Document applyStyle(Document doc) throws IOException;


	@Override
	public void lancerAction(ArrayList<File> files) {
		htmlFiles.clear();
		
	}


	@Override
	public abstract void parametrer();
	
	protected boolean isCleannable(Element element){
		boolean clean = true;
		for (Element e : element.getAllElements()) {
			if (baliseASauver.contains(e.tag().toString()) && !e.equals(element))
				clean = false;
		}
		return clean;
	}
	
		private void initBaliseASauver(){
			baliseASauver.add("img");
			baliseASauver.add("a");
			baliseASauver.add("ul");
			baliseASauver.add("li");
			baliseASauver.add("h1");
			baliseASauver.add("H1");
			baliseASauver.add("h2");
			baliseASauver.add("H2");
			baliseASauver.add("h3");
			baliseASauver.add("H3");
			baliseASauver.add("h4");
			baliseASauver.add("H4");
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
