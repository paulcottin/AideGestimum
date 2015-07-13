package actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JOptionPane;

import exceptions.NoPPDefine;
import interfaces.LancerAction;
import main.Principale;

public class ColorationPuces extends Observable implements LancerAction{

	private ArrayList<File> htmlFiles, ppFiles;
	private boolean running;
	private NoPPDefine noPPDefine;
	private boolean traiterPuceFleche;

	public ColorationPuces(ArrayList<File> files) {
		this.htmlFiles = new ArrayList<File>();
		this.ppFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
			else if (file.getAbsolutePath().endsWith(".htt"))
				ppFiles.add(file);
		}

		this.running = false;
		this.traiterPuceFleche = false;
		this.noPPDefine = new NoPPDefine();
	}

	@Override
	public void run() {
		if (ppFiles.size() > 0) {
			lancerActionAll();
		}else
			Principale.messageFin("Il faut définir une page principale !");
	}

	@Override
	public void lancerActionAll() {
		parametrer();
		running = true;
		update();
		for (File file : htmlFiles) {
			try {
				applyPuceStyle(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		running = false;
		update();
		if (noPPDefine.getPages().size() > 0)
			noPPDefine.display();
		Principale.messageFin("Mise à jour des puces ok !");
	}

	@Override
	public void fichiersSelectionnes(ArrayList<File> files) {
		lancerAction(files);
	}

	@Override
	public void lancerAction(ArrayList<File> files) {
		this.htmlFiles.clear();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
		}
	}

	@Override
	public void parametrer(){
		//traiterPuceFleche();
	}

	private void applyPuceStyle(File f) throws IOException{
		File tmp = new File("tmp");
		BufferedReader br = new BufferedReader(new FileReader(f));
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
		BufferedWriter saveBw = new BufferedWriter(new FileWriter(new File("save.html")));
		boolean inPuce = false;

		String ligne = "", couleur = null;

		while ((ligne = br.readLine()) != null){
			saveBw.write(ligne+"\r\n");
			if (ligne.contains("name=\"template\"")) {
				couleur = getCouleur(ligne);
				bw.write(ligne+"\r\n");
			}
			else if (ligne.contains("<li>") || ligne.contains("<li ") && couleur != null)
				bw.write(traitementLigne(ligne, couleur)+"\r\n");			
//			//Création de puce
//			else if (ligne.contains("·")){
//				traitementPuce("·", br, bw, ligne, inPuce);
//			}
//			else if (ligne.contains("Ã˜") && traiterPuceFleche){
//				traitementPuce("Ã˜", br, bw, ligne, inPuce);
//			}
			else
				bw.write(ligne+"\r\n");
		}

		if (couleur == null)
			noPPDefine.getPages().add(f.getName());

		saveBw.close();
		br.close();
		bw.close();

		Principale.fileMove(tmp, f);
		tmp.delete();
	}

	private void traitementPuce(String p, BufferedReader br, BufferedWriter bw, String ligne, boolean inPuce) throws IOException{
		if (!inPuce){
			inPuce = true;
			bw.write("<ul>\r\n");
		}
		//Si la ligne tient en une seule ligne et qu'il n'y a qu'une puce dans la ligne
		if (ligne.substring(ligne.indexOf(p)+1).contains("</p>") && !ligne.substring(ligne.indexOf(p)+1).contains(p)) {
			bw.write(creerPuce(ligne, p)+"\r\n");
		}
		//Si il y a plusieurs puces dans la ligne et que la ligne tient sur une seule ligne
		else if (ligne.substring(ligne.indexOf(p)+1).contains("</p>") && ligne.substring(ligne.indexOf(p)+1).contains(p)) {
			String[] tab = ligne.split(p);
			for (String string : tab) {
				bw.write(creerPuce(string, p)+"\r\n");
			}
		}
		//Si la puce ne tient pas en une ligne
		else if (!ligne.contains("</p>")){
			String puce = ligne;
			ligne = br.readLine();
			puce += ligne;
			while (ligne != null && (!ligne.contains(p) && !ligne.contains("</p>"))){
				puce += ligne;
				ligne = br.readLine();
			}
			bw.write(creerPuce(puce.replace("\r\n", " "), p)+"\r\n");
		}
		if (ligne != null && ligne.contains("</p>") && inPuce) {
			inPuce = false;
			bw.write("</ul>\r\n");
		}
	}

	private String creerPuce(String ligne, String puce){
		String l = ligne;
		l = l.replace("<p>", "");
		l = l.replace("</p>", "");
		l = l.replace(puce, "");
		l = l.replace("&#160;", "");
		return "<li>"+l+"</li>";

	}

	private String traitementLigne(String ligne, String couleur){
		int debutLi = ligne.indexOf("<li") + "<li".length();
		int finLi = ligne.substring(debutLi).indexOf(">")+debutLi;
		int debutSpan = ligne.indexOf("<span") + "<span".length();
		int finSpan = ligne.substring(debutSpan).indexOf(">")+debutSpan;

		String s = "<li"+traitementStyle(ligne.substring(debutLi, finLi), couleur, "li") + "<span"+
				traitementStyle(ligne.substring(debutSpan, finSpan), "black", "span")
		+ ligne.substring(finSpan+1);
		return s;
	}

	private String traitementStyle(String ligne, String couleur, String balise){
		//Si la ligne contient déjà un style, on rajoute la couleur en plus ou on la remplace
		if (ligne.contains("style=\"")) {
			//tableau des attributs de style
			String[]attrs = ligne.substring(ligne.indexOf("style=\"")+"style=\"".length(), ligne.length()).split(";");
			boolean isColor = false;
			for (String string : attrs) 
				if (string.startsWith("color:"))
					isColor = true;

			//Si il y a déjà un attribut couleur
			if (isColor) {
				int debutStyle = ligne.indexOf("style=\"") + ("style=\"").length();
				int finStyle = debutStyle + ligne.substring(debutStyle+1).indexOf("\"")+1;
				String style = ligne.substring(debutStyle, finStyle);

				int debut = style.indexOf("color: ") + "color: ".length();
				int fin = debut+ style.substring(debut+1).indexOf(";")+1;
				System.out.println(ligne.substring(0, debutStyle)+style.substring(0,debut)+couleur+style.substring(fin, style.length()-1)+ligne.substring(finStyle, ligne.length()-1)+";\">");
				return ligne.substring(0, debutStyle)+style.substring(0,debut)+couleur+style.substring(fin, style.length()-1)+ligne.substring(finStyle, ligne.length()-1)+";\">";
			}
			//Si il n'y a pas encore d'attribut couleur
			else{
				int debutStyle = ligne.indexOf("style=\"") + ("style=\"").length();
				System.out.println(ligne.substring(0, debutStyle)+"color: "+couleur+"; "+ligne.substring(debutStyle, ligne.length())+">");
				return ligne.substring(0, debutStyle)+"color: "+couleur+"; "+ligne.substring(debutStyle, ligne.length())+">";
			}
		}
		//Si il n'y a pas de style, on ajoute la couleur
		else{
			System.out.println(" style=\"color: "+couleur+";\">"+ligne);
			return " style=\"color: "+couleur+";\">"+ligne;
		}
	}

	private void traiterPuceFleche(){
		String[] choixTab = {"Oui", "Non"};
		String choix = (String) JOptionPane.showInputDialog(null, "Gérer les puces en forme de flèche en tant que puces", "Paramétrage des puces", 
				JOptionPane.QUESTION_MESSAGE, null, choixTab, choixTab[1]);
		if (choix != null) {
			if (choix.equals("Oui"))
				this.traiterPuceFleche = true;
			else
				traiterPuceFleche = false;
		}
		else
			traiterPuceFleche = false;
	}

	private String getCouleur(String ligne) throws IOException{
		String path = null;
		//Si le contenu de la balise content n'est pas vide
		if (ligne.contains(".htt\"")) {
			int debut = ligne.indexOf("content=\"") + "content=\"".length();
			int fin = debut+ ligne.substring(debut+1).indexOf("\"")+1;
			path = getAbsolutePathPP(ligne.substring(debut, fin));
			File file = new File(path);

			BufferedReader br = new BufferedReader(new FileReader(file));
			String l = "";

			while ((l = br.readLine()) != null){
				if (l.contains("<div style=\"background-color: ")){
					int d = l.indexOf("<div style=\"background-color: ") + "<div style=\"background-color: ".length();
					int f = debut+ l.substring(debut+1).indexOf(";")+1;
					br.close();
					return l.substring(d, f);
				}
			}
			br.close();
			return null;
		}else 
			return null;

	}

	/**
	 * Récupère le path d'une page principale en fonction de son nom ou un path
	 * @param path
	 * @return
	 */
	private String getAbsolutePathPP(String path){
		//Si un path est donné en paramètre on récupère le nom
		if (path.contains("\\")) {
			String[] t = path.split("\\");
			path = t[t.length-1];
		}
		//On renvoi le chemin absolu
		for (File file : ppFiles) {
			if (file.getName().equals(path)) {
				return file.getAbsolutePath();
			}
		}
		return null;
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
		//Ne rien faire
	}

	private void update(){
		setChanged();
		notifyObservers();
	}
}
