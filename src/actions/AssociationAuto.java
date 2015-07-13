package actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import exceptions.FichierUtilise;
import interfaces.LancerAction;
import interfaces.LongTask;
import main.Principale;

public class AssociationAuto extends Observable implements LancerAction, LongTask, Runnable{

	File sourceFile;
	ChoixPagePrincipale choixPP;
	ArrayList<String> paths, PP, ppPath;
	ArrayList<File> ppFiles;
	boolean running;

	public AssociationAuto(ArrayList<File> files) {
		choixPP = new ChoixPagePrincipale(files);
		this.PP = new ArrayList<String>();
		this.paths = new ArrayList<String>();
		this.ppPath = new ArrayList<String>();
		this.ppFiles = new ArrayList<File>();
		this.running = false;

		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htt"))
				ppFiles.add(file);
		}
	}

	@Override
	public void run() {
		lancerActionAll();	
	}

	@Override
	public void lancerActionAll() {
		getSourceFile();
		if (sourceFile != null) {
			try {
				checkEncodage();
				getPathAndPP();
				if (displayPP()) {
					applyStyle();
					Principale.messageFin("Application automatique effectuée avec succès");
				}
			} catch (FileSystemException e) {
				new FichierUtilise(sourceFile.getName());
			}
		}
		else
			Principale.messageFin("Veuillez renseigner un fichier");
		this.running = false;
		update();
	}

	@Override
	public void lancerAction(ArrayList<File> files) {
		
	}
	
	@Override
	public void fichiersSelectionnes(ArrayList<File> files) {
		lancerAction(files);
	}
	
	@Override
	public void parametrer(){
		
	}

	private void getSourceFile() {
		JOptionPane.showMessageDialog(null, "<html>Veuillez sélectionner le fichier csv (séparateur ';') contenant les liens<br/>"
				+ "Ce fichier doit être sans en-tête, les chemins à gauche, les pages principales à droite</html>");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
			sourceFile = fileChooser.getSelectedFile();
		else
			sourceFile = null;
	}

	private void getPathAndPP(){		
		try {
			BufferedReader br = new BufferedReader(new FileReader(sourceFile));
			String ligne = "";

			while ((ligne = br.readLine()) != null) {
				String[] tab = ligne.split(";");
				paths.add(tab[0]);
				PP.add(tab[tab.length-1]);
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean displayPP(){
		ArrayList<String> tmp = new ArrayList<String>();
		//On récupère toutes les pages principales du fichier source
		for (String string : PP) {
			if (!tmp.contains(string) && !string.equals("0")) {
				tmp.add(string);
			}
		}

		//On met dans un tableau les différentes pp (htt) qui existent dans le projet
		String[] tab = new String[ppFiles.size()];
		for (int i = 0; i < ppFiles.size(); i++) {
			tab[i] = ppFiles.get(i).getName();
		}

		//Si il y a des pages principales crées
		if (tab.length > 0) {
			//Pour chacune des pages du fichier source on demande de faire un match avec une page principale htt
			for (int i = 0; i < tmp.size(); i++) {
				String name =	(String) JOptionPane.showInputDialog(null, 
						tmp.get(i),
						"Correspondance",
						JOptionPane.QUESTION_MESSAGE,
						null,
						tab, tab[0]);

				updatePath(tmp.get(i), name);
			}
			return true;
		}else {
			Principale.messageFin("Il faut d'abord définir une page principale !");
			return false;
		}
			
	}

	private void applyStyle(){
		this.running = true;
		update();
		for (int i = 0; i < PP.size(); i++) {
			if (!PP.get(i).equals(0)) {
				//on fixe la pp
				choixPP.setPagePath(PP.get(i));
				//On fixe les fichiers sur lesquels on applique le traitement
				choixPP.getHtmlFiles().clear();
				choixPP.getHtmlFiles().add(new File(paths.get(i)));
				//On applique
				choixPP.applyPagePrincipale();
			}
		}
	}

	private void updatePath(String pageP, String name){
		for (int i = 0; i < PP.size(); i++) {
			if (PP.get(i).equals(pageP)){
				PP.set(i, nameToPath(name));
			}

		}
	}

	private String nameToPath(String name){
		for (File file : ppFiles) {
			if (file.getName().equals(name)) {
				return file.getAbsolutePath();
			}
		}
		return null;
	}

	private void checkEncodage() throws FileSystemException {
		BufferedReader br = null;
		File tmp = new File("tmp.csv");
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(sourceFile));
			bw = new BufferedWriter(new FileWriter(tmp));

			String ligne = "";

			while ((ligne = br.readLine()) != null){
				if (ligne.contains("?") || ligne.contains("‚") || ligne.contains("…") || 
						ligne.contains("‡") || ligne.contains("ˆ") || ligne.contains("‰") || ligne.contains("Š")  || ligne.contains("—")
						|| ligne.contains("“")  || ligne.contains("–")  || ligne.contains("Œ")  || ligne.contains("‹")) {
					String l = ligne;
					if (ligne.contains("?"))
						l = l.replaceAll("\\?", "’");
					if (ligne.contains("‚"))
						l = l.replaceAll("‚", "é");
					if (ligne.contains("…"))
						l = l.replaceAll("…", "à");
					if (l.contains("‡"))
						l = l.replaceAll("‡", "ç");
					if (l.contains("ˆ"))
						l = l.replaceAll("ˆ", "ê");
					if (l.contains("‰"))
						l = l.replaceAll("‰", "ë");
					if (l.contains("Š"))
						l = l.replaceAll("Š", "è");
					if (l.contains("—"))
						l = l.replaceAll("—", "ù");
					if (l.contains("“"))
						l = l.replaceAll("“", "ô");
					if (l.contains("–"))
						l = l.replaceAll("–", "û");
					if (l.contains("Œ"))
						l = l.replaceAll("Œ", "î");
					if (l.contains("‹"))
						l = l.replaceAll("‹", "ï");
					if (l.contains("ƒ"))
						l = l.replaceAll("ƒ", "â");
					bw.write(l+"\r\n");
				}
				else
					bw.write(ligne+"\r\n");
			}
			bw.close();
			br.close();
		} catch (IOException e) {
			try {
				bw.close();
				br.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();

		}
		try {
			Principale.fileMove(tmp, sourceFile);
		} catch (FileSystemException fse){
			throw fse;
		}
	}

	private void update(){
		setChanged();
		notifyObservers();
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
}
