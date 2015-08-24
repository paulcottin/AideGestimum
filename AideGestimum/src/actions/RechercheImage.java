package actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Observable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import exceptions.ParametrageError;
import interfaces.LancerAction;
import interfaces.LongTask;
import main.Principale;
import utilitaires.Utils;

public class RechercheImage extends Observable implements LancerAction, LongTask{

	private ArrayList<File> imgFile, htmFile, folderFT;
	private boolean running;
	private String fileProcessing, folderPath, fileAbsolutePath;
	private Utils utils;

	public RechercheImage(ArrayList<File> files, String folderPath) {
		utils = new Utils();
		imgFile = new ArrayList<File>();
		htmFile = new ArrayList<File>();
		folderFT = new ArrayList<File>();
		reloadFiles(files);
		running = false;
		this.folderPath = folderPath;
	}
	@Override
	public void run() {
		try {
			parametrer();
			running = true;
			setChanged();
			notifyObservers();
			lancerActionAll();
			Principale.messageFin("Fin de la recherche des images");
		} catch (ParametrageError e) {
			e.printMessage();
		}
		running = false;
		setChanged();
		notifyObservers();
	}

	@Override
	public void lancerActionAll() throws ParametrageError {
		String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		for (File file : htmFile) {
			fileProcessing = file.getName();
			fileAbsolutePath = file.getAbsolutePath();
			setChanged();
			notifyObservers();
			File tmp = new File(generateString(5, chars)+".jlb");
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmp), "UTF-8"));
				String ligne = "", txt = "";


				while ((ligne = br.readLine()) != null){
					txt += ligne+"\r\n";
				}

				Document doc = Jsoup.parse(txt);
				doc = applyStyle(doc, file.getName());
				String html = doc.html();

				//D�commente les balises RoboHelp, comment�e automatiquement par
				//la libraire JSoup.
				html = html.replace("<!--?", "<?");
				html = html.replace("?-->", "?>");

				bw.write(html);
				br.close();
				bw.close();

				Principale.fileMove(tmp, file);
				tmp.delete();

			} catch (IOException e) {
				e.printStackTrace();
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

	@Override
	public void lancerAction(ArrayList<File> files) {
	}

	@Override
	public void parametrer() throws ParametrageError {
	}

	private Document applyStyle(Document doc, String fileName){
		Elements imgs = doc.select("img");
		for (Element element : imgs) {
			String path = getRelativeImgFilePath(element.attr("src"), fileName);
			element.attr("src", path);
		}
		return doc;
	}

	private String getRelativeImgFilePath(String imgName, String fileName){
		String nom = "";
		//Si on a un chemin
		if (imgName.contains("\\")) {
			String[] tab = imgName.split("\\\\");
			nom = tab[tab.length-1];
		}else if (imgName.contains("/")) {
			String[] tab = imgName.split("/");
			nom = tab[tab.length-1];
		}
		//Si on a seulement un nom
		else 
			nom = imgName;
		//Si ce nom est de la forme image00.png (ou du mm style), retourne imgName, sinon on recherche
		if (nom.matches("(i|I)mages?_?[0-9]*\\..*") || nom.matches("(p|P)ictures?_?[0-9]*\\..*")) {
			if (utils.sameFileName(folderFT, fromFileToFolder(fileName))) {
				int index = utils.indexInArray(folderFT, fromFileToFolder(fileName));
				return getRelativePath(folderFT.get(index)+"\\"+nom, nom);
			}
			else
				return imgName;
		}
		//on recherche dans la liste des images l'absolute file path qu'on remplace par le relatif
		for (File file : imgFile) {
			if (file.getName().equals(nom)) {
				return getRelativePath(file.getAbsolutePath(), nom);
			}
		}
		//on retourne le r�sultat, imgName si pas trouv�
		return imgName;
	}
	
	private String getRelativePath(String absolutePath, String nom) {
		String relativePath = absolutePath.substring(folderPath.length()+1);
		int nbFolderImg = relativePath.split("\\\\").length-1;
		int nbFolderFile = fileAbsolutePath.substring(folderPath.length()+1).split("\\\\").length-1;
		//Si l'image est plus haute que le fichier dans l'arborescence
		if (nbFolderImg < nbFolderFile) {
			String s = "";
			for (int i = 0; i <= nbFolderFile-((nbFolderImg == 0) ? 1 : nbFolderImg); i++)
				s += "../";
			for (int i = 0; i < nbFolderImg; i++)
				s += relativePath.split("\\\\")[i]+"/";
			return s+nom;
		}
		//Si l'image est dans le m�me �tage de dossier que le fichier, on remonte du nombre et on redescend du m�me nombre
		else if (nbFolderFile == nbFolderImg) {
			String s = "";
			for (int i = 0; i < nbFolderFile; i++)
				s += "../";		
			for (int i = 0; i < nbFolderImg; i++)
				s += relativePath.split("\\\\")[i]+"/";
			return s+nom;
		}
		//si l'image est dans un sous-dossier de celui du fichier
		else {
			String s = "";
			for (int i = 0; i < nbFolderFile; i++)
				s += "../";					
			return s+absolutePath.substring(folderPath.length()+1).replace("\\", "/");
		}
	}

	@Override
	public void reloadFiles(ArrayList<File> files) {
		imgFile.clear();
		htmFile.clear();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".jpg") || file.getAbsolutePath().endsWith(".png") || file.getAbsolutePath().endsWith(".gif"))
				imgFile.add(file);
			else if (file.getAbsolutePath().endsWith(".htm"))
				htmFile.add(file);
			else if (file.isDirectory() && file.getName().endsWith("_fichiers"))
				folderFT.add(file);
		}
	}
	
	private String fromFileToFolder(String fileName) {
		if (fileName.endsWith(".htm"))
			return fileName.substring(0, fileName.length()-".htm".length()) + "_fichiers";
		else return "";
	}

	@Override
	public void fichiersSelectionnes(ArrayList<File> files) {
		htmFile.clear();
		htmFile.addAll(files);
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
	public void onProgressBarDispose() {
		
	}
	
	@Override
	public String getFichierTraitement() {
		return fileProcessing;
	}
	
	@Override
	public String getTitre() {
		return "Recherce des chemins des images";
	}
}
