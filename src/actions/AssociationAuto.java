package actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import exceptions.FichierNonTrouve;
import exceptions.ParametrageError;
import interfaces.Action;

public class AssociationAuto extends Action {

	File sourceFile;
	ChoixPagePrincipale choixPP;
	ArrayList<String> paths, PP;
	FichierNonTrouve fichierNontrouve;

	public AssociationAuto(ArrayList<File> files) {
		super(files);
		choixPP = new ChoixPagePrincipale(files);
		this.PP = new ArrayList<String>();
		this.paths = new ArrayList<String>();
		this.fichierNontrouve = new FichierNonTrouve();
		messageFin = "Application automatique effectu�e avec succ�s";
		intitule = "Association automatique";
	}

	@Override
	public void parametrer() throws ParametrageError{
		try {
			getSourceFile();
			getPathAndPP();
			displayPP();
		} catch (ParametrageError e) {
			throw e;
		}
	}

	private void getSourceFile() throws ParametrageError {
		JOptionPane.showMessageDialog(null, "<html>Veuillez s�lectionner le fichier csv (s�parateur ';') contenant les liens<br/>"
				+ "Ce fichier doit �tre sans en-t�te, les chemins � gauche, les pages principales � droite</html>");
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) 
			sourceFile = fileChooser.getSelectedFile();
		else
			throw new ParametrageError("Il faut s�lectionner un fichier csv valide !");
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

	private boolean displayPP() throws ParametrageError{
		ArrayList<String> tmp = new ArrayList<String>();
		//On r�cup�re toutes les pages principales du fichier source
		for (String string : PP) {
			if (!tmp.contains(string) && !string.equals("0")) {
				tmp.add(string);
			}
		}

		//On met dans un tableau les diff�rentes pp (htt) qui existent dans le projet
		String[] tab = new String[ppFiles.size()];
		for (int i = 0; i < ppFiles.size(); i++) {
			tab[i] = ppFiles.get(i).getName();
		}

		//Si il y a des pages principales cr�es
		if (tab.length > 0) {
			//Pour chacune des pages du fichier source on demande de faire un match avec une page principale htt
			for (int i = 0; i < tmp.size(); i++) {
				String name =	(String) JOptionPane.showInputDialog(null, 
						tmp.get(i),
						"Correspondance",
						JOptionPane.QUESTION_MESSAGE,
						null,
						tab, tab[0]);

				if (name == null)
					throw new ParametrageError("Il faut faire la correspondance avec TOUTES les pages !");
				updatePath(tmp.get(i), name);
			}
			return true;
		}else {
			throw new ParametrageError("Il faut d'abord d�finir au moins une page principale !");
		}

	}

	@Override
	protected Document applyStyle(Document doc) throws IOException {
		for (int i = 0; i < PP.size(); i++) {
			if (!PP.get(i).equals(0)) {
				//on cr�e le doc de la pp
				choixPP.setPagePath(PP.get(i));
				//On fixe les fichiers sur lesquels on applique le traitement
				try {
					Document d = Jsoup.parse(new File(paths.get(i)), "utf-8");
					//On applique
					choixPP.applyStyle(d);
				} catch (FileNotFoundException e) {
					fichierNontrouve.add(paths.get(i));
				}
			}
		}
		return doc;
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

	/**
	 * Plus besoin depuis qu'on sait lire avec un encodage pr�cis un fichier
	 * @return
	 */
//	private void checkEncodage() throws FileSystemException {
//		BufferedReader br = null;
//		File tmp = new File("tmp.csv");
//		BufferedWriter bw = null;
//		try {
//			br = new BufferedReader(new FileReader(sourceFile));
//			bw = new BufferedWriter(new FileWriter(tmp));
//
//			String ligne = "";
//
//			while ((ligne = br.readLine()) != null){
//				if (ligne.contains("?") || ligne.contains("�") || ligne.contains("�") || 
//						ligne.contains("�") || ligne.contains("�") || ligne.contains("�") || ligne.contains("�")  || ligne.contains("�")
//						|| ligne.contains("�")  || ligne.contains("�")  || ligne.contains("�")  || ligne.contains("�")) {
//					String l = ligne;
//					if (ligne.contains("?"))
//						l = l.replaceAll("\\?", "�");
//					if (ligne.contains("�"))
//						l = l.replaceAll("�", "�");
//					if (ligne.contains("�"))
//						l = l.replaceAll("�", "�");
//					if (l.contains("�"))
//						l = l.replaceAll("�", "�");
//					if (l.contains("�"))
//						l = l.replaceAll("�", "�");
//					if (l.contains("�"))
//						l = l.replaceAll("�", "�");
//					if (l.contains("�"))
//						l = l.replaceAll("�", "�");
//					if (l.contains("�"))
//						l = l.replaceAll("�", "�");
//					if (l.contains("�"))
//						l = l.replaceAll("�", "�");
//					if (l.contains("�"))
//						l = l.replaceAll("�", "�");
//					if (l.contains("�"))
//						l = l.replaceAll("�", "�");
//					if (l.contains("�"))
//						l = l.replaceAll("�", "�");
//					if (l.contains("�"))
//						l = l.replaceAll("�", "�");
//					bw.write(l+"\r\n");
//				}
//				else
//					bw.write(ligne+"\r\n");
//			}
//			bw.close();
//			br.close();
//		} catch (IOException e) {
//			try {
//				bw.close();
//				br.close();
//			} catch (IOException e1) {
//				e1.printStackTrace();
//			}
//			e.printStackTrace();
//
//		}
//		try {
//			Principale.fileMove(tmp, sourceFile);
//		} catch (FileSystemException fse){
//			throw fse;
//		}
//	}

	public FichierNonTrouve getFichierNontrouve() {
		return fichierNontrouve;
	}

	public void setFichierNontrouve(FichierNonTrouve fichierNontrouve) {
		this.fichierNontrouve = fichierNontrouve;
	}
}
