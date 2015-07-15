package actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystemException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import interfaces.Action;
import main.Principale;

public class AssociationAuto extends Action {

	File sourceFile;
	ChoixPagePrincipale choixPP;
	ArrayList<String> paths, PP, ppPath;

	public AssociationAuto(ArrayList<File> files) {
		super(files);
		choixPP = new ChoixPagePrincipale(files);
		this.PP = new ArrayList<String>();
		this.paths = new ArrayList<String>();
		this.ppPath = new ArrayList<String>();
		messageFin = "Application automatique effectuée avec succès";
		intitule = "Association automatique";
	}

	@Override
	public void parametrer(){
		getSourceFile();
		try {
			checkEncodage();
		} catch (FileSystemException e) {
			e.printStackTrace();
		}
		getPathAndPP();
		displayPP();
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

	@Override
	protected Document applyStyle(Document doc) throws IOException {
		for (int i = 0; i < PP.size(); i++) {
			if (!PP.get(i).equals(0)) {
				//on crée le doc de la pp
				Document d = Jsoup.parse(new File(PP.get(i)), "utf-8");
				choixPP.setPagePath(PP.get(i));
				//On fixe les fichiers sur lesquels on applique le traitement
				choixPP.getHtmlFiles().clear();
				choixPP.getHtmlFiles().add(new File(paths.get(i)));
				//On applique
//				choixPP.
			}
		}
		return null;
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
}
