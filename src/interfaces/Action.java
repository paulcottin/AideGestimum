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

import actions.AssociationAuto;
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

	/**
	 * Contructeur de la classe.
	 * Trois listes de fichiers sont instanci�es et remplies en fonction de l'extension des fichiers du projet.
	 * Le boolean running est initialis� � false, il sert � savoir lorsqu'un script est en train d'�tre ex�cut�
	 * la liste de String "balise � sauver" est une contenant des balises de mises en forme particuli�re comme 
	 * ul, li, img, h1-5, tr, td ... 
	 * Cette liste permettra ensuite de savoir si on doit supprimer toutes les balises de mise en page 
	 * (comme <span> par exemple) dans une balise <p>, pour appliquer le style d�finit dans le CSS ou 
	 * si il ne faut pas supprimer l'int�rieur de cette balise p de peur de perdre de l'information. 
	 * @param files
	 */
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


	/**
	 * C'est la m�thode la premi�re appel�e.
	 * Elle g�re les erreurs de param�trages et l'affichage de la barre 
	 * de progression (avec le boolean running)
	 * Elle supprime �galement les fichiers temporaires � la fin de l'ex�cution.
	 */
	@Override
	public void run() {
		try {
			lancerActionAll();
		} catch (ParametrageError e) {
			e.printMessage();
		}
		supprFichiersTemp();
		running = false;
		update();
	}


	/**
	 * Cette m�thode permet de r�cup�rer les fichiers s�lectionn�s dans la fen�tre de s�lection, afin 
	 * d'appliquer le traitement sur eux seuls.
	 */
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


	/**
	 * Lance le script sur tous les fichiers s�lectionn�s.
	 * Pour chaque fichier on applique le script.
	 * A la fin on g�re l'affichage de certaines exceptions comme celles qui recensent les pages sans 
	 * feuilles de style ou sans pages principales.
	 */
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
					e.printStackTrace();
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
				String msg = "Ces pages ont des probl�me avec leur CSS<br/>Traitement du texte des puces impossible !<br/><ul>";
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
		}
		else if (this instanceof AssociationAuto) {
			ArrayList<String> list = ((AssociationAuto) this).getFichierNontrouve().getPages();
			if (list.size() > 0) {
				String msg = "Ces fichiers n'ont pas �t� trouv� !<br/><ul>";
				for (String string : list) {
					msg += "<li>"+string+"</li>";
				}
				msg += "</ul>";
				((AssociationAuto) this).getFichierNontrouve().setMessage(msg);
				((AssociationAuto) this).getFichierNontrouve().printMessage();
			}else
				Principale.messageFin(messageFin);
		}
		else
			Principale.messageFin(messageFin);
	}

	/**
	 * Supprime les fichiers temporaires (extension .jlb)
	 */
	private void supprFichiersTemp(){
		String dir = System.getProperty("user.dir");
		File[] files = (new File(dir)).listFiles();
		for (File file : files)
			if (file.getName().endsWith(".jlb"))
				file.delete();
	}

	/**
	 * M�thode qui applique le script sur une page.
	 * Cette m�thode lit le fichier, le parse avec l'aide de la bivlioth�que JSoup puis passe
	 * le document ainsi form� � la fonction applyStyle(Document doc) qui va lui appliquer le script proprement dit.
	 * Apr�s que le script soit appliqu�, cette m�thode �crit le r�sultat dans un fichier temporaire et
	 * remplace le fichier de base par le fichier temporaire.
	 * @param file : le fichier qui va �tre trait�
	 * @throws IOException
	 */
	private void applyStyleHelper(File file) throws IOException{
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		File tmp = new File(generateString(5, chars)+".jlb");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), "UTF-8"));
		String ligne = "", txt = "";

		while ((ligne = br.readLine()) != null){
			txt += ligne+"\r\n";
		}

		Document doc = Jsoup.parse(txt);
		doc = applyStyle(doc);
		String html = doc.html();

		//D�commente les balises RoboHelp, comment�e automatiquement par
		//la libraire JSoup.
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

	/**
	 * G�n�re une cha�ne de caract�re pour le nom d'un fichier temporaire.
	 * @param length : longueur de la chaine
	 * @param chars : Cha�ne de caract�re servant de base � la g�n�ration.
	 * @return
	 */
	private String generateString(int length, String chars) {
		StringBuilder  pass = new StringBuilder (chars.length());
		for (int x = 0; x < length; x++) {
			int i = (int) (Math.random() * chars.length());
			pass.append(chars.charAt(i));
		}
		return pass.toString();
	}

	/**
	 * M�thode impl�ment� par chaque classe du package actions (chaque script) qui fait le travail
	 * particulier.
	 * @param doc le document d'origine (format JSoup)
	 * @return doc : le m�me document modifi� par le script
	 * @throws IOException
	 */
	protected abstract Document applyStyle(Document doc) throws IOException;

	/**
	 * M�thode inutilis�e mais indispensable.
	 */
	@Override
	public void lancerAction(ArrayList<File> files) {
		htmlFiles.clear();
	}

	/**
	 * M�thode immpl�ment�e par chaque script dans laquelle on demande (si besoin) des informations
	 * � l'utilisateur afin de param�trer le script.
	 */
	@Override
	public abstract void parametrer() throws ParametrageError;

	/**
	 * Affiche une bo�te de dialogue pour r�cup�rer un fichier CSS parmi tous ceux du projet
	 * @param titre : tire de la bo�te de dialogue.
	 * @param message : Message de la bo�te de dialogue
	 * @return file : Fichier correspondant au fichier s�lectionn� par l'utilisateur.
	 * @throws ParametrageError : Si l'utilisateur annule ou ferme la bo�te de dialogue.
	 */
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
			throw new ParametrageError("Il faut s�lectionner une feuille de style !");

		ArrayList<String> tmp = new ArrayList<String>();
		for (String string : cssFiles) {
			tmp.add(string);
		}

		cssFilePath = this.cssFiles.get(tmp.indexOf(cssFilePath)).getAbsolutePath();
		return new File(cssFilePath);
	}

	/**
	 * Affiche une bo�te de dialogue pour la s�lection d'une classe CSS dans un fichier CSS
	 * @param file : Fichier dans lequel chercher
	 * @param titre : Titre de la bo�te de dialogue
	 * @param message : Message de la bo�te de dialogue
	 * @return classe s�lectionn�e par l'utilisateur
	 * @throws ParametrageError : Si l'utilisateur annule ou ferme la bo�te de dialogue.
	 */
	protected String cssClass(File file, String titre, String message) throws ParametrageError{
		String[] styles = afficheCSSClasses(getCssClass(file));
		String style =	(String) JOptionPane.showInputDialog(null, 
				message,
				titre,
				JOptionPane.QUESTION_MESSAGE,
				null,
				styles, styles[0]);

		if (style == null)
			throw new ParametrageError("Il faut s�lectionner une classe CSS ! ");

		style = getCSSBalise(style);
		return style;
	}

	/**
	 * 
	 * @param fichier CSS
	 * @return toutes les classes CSS pr�sentes dans ce fichier
	 */
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
	 * D'un titre affich� donne le nom de la balise ou de la classe CSS
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

	/**
	 * inverse de la m�thode ci-dessus
	 * @param classes
	 * @return
	 */
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

	/**
	 * Affiche une boite de dialogue permettant de s�lectionner une page principale parmi toutes celles du projet
	 * @return : le chemin absolu de la page s�lectionn�e.
	 * @throws ParametrageError : Si l'utilisateur annule ou ferme la bo�te de dialogue.
	 */
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
			throw new ParametrageError("Il faut s�lectionner une page principale !");

		ArrayList<String> tmp = new ArrayList<String>();
		for (File file: ppFiles) {
			tmp.add(file.getName());
		}

		String path = ppFiles.get(tmp.indexOf(page)).getPath();
		return path;
	}

	/**
	 * V�rifie si l'�l�ment Jsoup pass� en param�tre contient comme fils des balises � ne pas supprim�e
	 * (list�es dans baliseASauvegardee).
	 * @param element
	 * @return true si les balises ne sont pas trouv�es, false sinon
	 */
	protected boolean isCleannable(Element element){
		boolean clean = true;
		for (Element e : element.getAllElements()) {
			if (baliseASauver.contains(e.tag().toString()) && !e.equals(element))
				clean = false;
		}
		return clean;
	}

	/**
	 * 
	 * @param path d'un fichier CSS
	 * @return le path absolu de ce fichier
	 */
	protected String getFullCSSPath(String path){
		String name = null;
		if (path.contains("/")) {
			String[] tab = path.split("/");
			name = tab[tab.length-1];
		}
		else if (path.contains("\\")){
			String[] tab = path.split("\\\\");
			name = tab[tab.length-1];
		}
		else
			name = path;
		for (File file: cssFiles) {
			if (file.getName().equals(name))
				return file.getAbsolutePath();
		}
		return null;
	}

	/**
	 * initialisation de la liste de balises � ne pas d�truire.
	 */
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

	/**
	 * factorisation de code pour notifier les observer que le mod�le � chang�
	 */
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
