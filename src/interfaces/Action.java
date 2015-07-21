package interfaces;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import actions.ColorationPuces;
import actions.NoPP;
import exceptions.ParametrageError;
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
		try {
			lancerActionAll();
		} catch (ParametrageError e) {
			e.printMessage();
		}
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
	public void lancerActionAll() throws ParametrageError{
		parametrer();
		running = true;
		update();
		for (File file : htmlFiles) {
			synchronized (file) {
				System.out.println(intitule+" : "+file.getName());
				try {
					applyStyleHelper(file);
				} catch (NullPointerException e) {
					System.out.println("erreur : "+file.getAbsolutePath());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (this instanceof ColorationPuces) {
			ArrayList<String> list = ((ColorationPuces) this).getNoPPDefine().getPages();
			if (list.size() > 0) {
				String msg = "Ces pages n'ont pas de page principale.<br/>Coloration des puces impossible !<br/><ul>";
				for (String string : list) {
					msg += "<li>"+string+"</li>";
				}
				msg += "</ul>";
				Principale.messageFin(msg);
			}else
				Principale.messageFin(messageFin);
			list = ((ColorationPuces) this).getNoCSSDefine().getPages();
			if (list.size() > 0) {
				String msg = "Ces pages ont des problème avec leur CSS<br/>Traitement du texte des puces impossible !<br/><ul>";
				for (String string : list) {
					msg += "<li>"+string+"</li>";
				}
				msg += "</ul>";
				Principale.messageFin(msg);
			}else
				Principale.messageFin(messageFin);
		}
		else if (this instanceof NoPP) {
			ArrayList<String> list = ((NoPP) this).getException().getPages();
			if (list.size() > 0) {
				String msg = "Ces pages n'ont pas de page principale !<br/><ul>";
				for (String string : list) {
					msg += "<li>"+string+"</li>";
				}
				msg += "</ul>";
				Principale.messageFin(msg);
			}else
				Principale.messageFin(messageFin);
		}else
			Principale.messageFin(messageFin);
	}

	private void applyStyleHelper(File file) throws IOException{
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		File tmp = new File(generateString(5, chars));
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), "UTF-8"));
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

		synchronized (tmp) {
			synchronized (file) {
				Principale.fileMove(tmp, file);
				tmp.delete();
			}
		}
	}

	private String generateString(int length, String chars) {
		StringBuilder  pass = new StringBuilder (chars.length());
		for (int x = 0; x < length; x++) {
			int i = (int) (Math.random() * chars.length());
			pass.append(chars.charAt(i));
		}
		return pass.toString();
	}

	protected abstract Document applyStyle(Document doc) throws IOException;


	@Override
	public void lancerAction(ArrayList<File> files) {
		htmlFiles.clear();
	}


	@Override
	public abstract void parametrer() throws ParametrageError;

	protected File cssFile(String titre, String message) throws ParametrageError{
		String cssFilePath = null;
		String[] cssFiles = new String[this.cssFiles.size()];
		for (int i = 0; i < this.cssFiles.size(); i++) {
			cssFiles[i] = this.cssFiles.get(i).getPath();
		}

		cssFilePath =	(String) JOptionPane.showInputDialog(null, 
				message,
				titre,
				JOptionPane.QUESTION_MESSAGE,
				null,
				cssFiles, cssFiles[0]);

		if (cssFilePath == null)
			throw new ParametrageError("Il faut sélectionner une feuille de style !");

		ArrayList<String> tmp = new ArrayList<String>();
		for (String string : cssFiles) {
			tmp.add(string);
		}

		cssFilePath = this.cssFiles.get(tmp.indexOf(cssFilePath)).getAbsolutePath();
		return new File(cssFilePath);
	}

	protected String cssClass(File file, String titre, String message) throws ParametrageError{
		String[] styles = afficheCSSClasses(getCssClass(file));
		String style =	(String) JOptionPane.showInputDialog(null, 
				message,
				titre,
				JOptionPane.QUESTION_MESSAGE,
				null,
				styles, styles[0]);

		if (style == null)
			throw new ParametrageError("Il faut sélectionner une classe CSS ! ");
		
		style = getCSSBalise(style);
		return style;
	}

	protected ArrayList<String> getCssClass(File file) {
		ArrayList<String> reponse = new ArrayList<String>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			String ligne = "";
			while((ligne = br.readLine()) != null){
				if (ligne.contains("{")) {
					String classe = ligne.substring(0, ligne.indexOf("{")-1);
					if (classe.contains("."))
						classe = classe.split("\\.")[1];
					reponse.add(classe);
				}
			}

			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return reponse;
	}

	/**
	 * D'un titre affiché donne le nom de la balise ou de la classe CSS
	 * @param classeAffichee
	 * @return
	 */
	private String getCSSBalise(String classeAffichee){
		if (classeAffichee.equals("Normal"))
			return "p";
		else if (classeAffichee.matches("Titre [0-9]"))
			return "H"+classeAffichee.substring("Titre ".length());
		else
			return classeAffichee;
	}

	private String[] afficheCSSClasses(ArrayList<String> classes){
		String[] styles = new String[classes.size()];
		for (int i = 0; i < classes.size(); i++) {
			if (classes.get(i).equals("p"))
				styles[i] = "Normal";
			else if (classes.get(i).matches("(H|h)[0-9]"))
				styles[i] = "Titre "+classes.get(i).substring(1);
			else
				styles[i] = classes.get(i);
		}
		return styles;
	}

	protected String getPPPath() throws ParametrageError{
		String[] pp = new String[ppFiles.size()];
		for (int i = 0; i < ppFiles.size(); i++) {
			pp[i] = ppFiles.get(i).getName();
		}

		String page =	(String) JOptionPane.showInputDialog(null, 
				"Quel page principale voulez-vous appliquer",
				"Modification de la page principale",
				JOptionPane.QUESTION_MESSAGE,
				null,
				pp, pp[0]);
		
		if (page == null)
			throw new ParametrageError("Il faut sélectionner une page principale !");
		
		ArrayList<String> tmp = new ArrayList<String>();
		for (File file: ppFiles) {
			tmp.add(file.getName());
		}

		String path = ppFiles.get(tmp.indexOf(page)).getPath();
		return path;
	}

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
		baliseASauver.add("tr");
		baliseASauver.add("td");
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
