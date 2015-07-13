package actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;

import javax.swing.JOptionPane;

import exceptions.FichierNonTrouve;
import interfaces.LancerAction;
import main.Principale;

public class ChoixPagePrincipale extends Observable implements LancerAction{

	private ArrayList<File> htmlFiles, ppFiles;
	private String page, pagePath;
	private boolean running;

	public ChoixPagePrincipale(ArrayList<File> files) {
		this.htmlFiles = new ArrayList<File>();
		this.ppFiles = new ArrayList<File>();
		this.running = false;

		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm")) 
				htmlFiles.add(file);
			else if (file.getAbsolutePath().endsWith(".htt"))
				ppFiles.add(file);
		}
	}
	
	public void run(){
		if (ppFiles.size() > 0) {
			lancerActionAll();
			running = false;
			update();
		}else
			Principale.messageFin("Il faut définir des pages principales");
	}

	@Override
	public void lancerActionAll() {
		parametrer();
		if (page != null) {
			running = true;
			update();
			applyPagePrincipale();
			Principale.messageFin("Mise à jour de la page principale finie");
		}
		else
			Principale.messageFin("Il faut sélectioner une page principale");
	}

	public void lancerAction(ArrayList<File> files){
		this.htmlFiles.clear();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".htm"))
				htmlFiles.add(file);
		}
	}
	
	@Override
	public void fichiersSelectionnes(ArrayList<File> files) {
		lancerAction(files);
	}
	
	@Override
	public void parametrer(){
		getPagePrincipales();
	}

	private void getPagePrincipales(){
		String[] pp = new String[ppFiles.size()];
		for (int i = 0; i < ppFiles.size(); i++) {
			pp[i] = ppFiles.get(i).getName();
		}

		page =	(String) JOptionPane.showInputDialog(null, 
				"Quel page principale voulez-vous appliquer",
				"Modification de la page principale",
				JOptionPane.QUESTION_MESSAGE,
				null,
				pp, pp[0]);
		ArrayList<String> tmp = new ArrayList<String>();
		for (File file: ppFiles) {
			tmp.add(file.getName());
		}
		pagePath = ppFiles.get(tmp.indexOf(page)).getPath();
	}

	public void applyPagePrincipale(){
		for (File file : htmlFiles) {
			try {
				applyPagePrincipaleHelper(file);
			} catch (FileNotFoundException fnf){
				new FichierNonTrouve(file.getName());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void applyPagePrincipaleHelper(File f) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(f));
		File tmp = new File(f.getAbsolutePath()+"_tmp");
		BufferedWriter bw = new BufferedWriter(new FileWriter(tmp));
		
		boolean isPP = false, isHeader = false, isFooter = false;
		String ligne = "";

		while ((ligne = br.readLine()) != null){
			if (ligne.contains("type=\"header\""))
				isHeader = true;
			if (ligne.contains("type=\"footer\""))
				isFooter = true;
			//Si on tombe sur une pp on met isPP à true et on modifie la page principale
			if (ligne.contains("template") && ligne.contains(".htt")){
				isPP = true;
				int debut = ligne.indexOf("content=\"")+"content=\"".length();
				int fin = debut + ligne.substring(debut).indexOf("\"");
				String avant = ligne.substring(0, debut);
				String apres = ligne.substring(fin, ligne.length());
				bw.write(avant+pagePath+apres+"\r\n");
			}
			//Si on arrive à <title> et qu'on a pas trouvé de page principale il faut en ajouter une 
			else if (ligne.contains("<title>") && !isPP) {
				bw.write("<meta name=\"template\" content=\""+pagePath+"\" />\r\n");
				bw.write(ligne+"\r\n");
			}
			//Si on arrive au body on rajoute le  header
			else if (ligne.contains("<body") && !isHeader){
				bw.write("<?rh-placeholder type=\"header\" ?>\r\n");
				bw.write("<body>\r\n");
			}
			//Si on arrive à la propriété fontValue on met à jour
			else if (ligne.contains("name=\"FontInfo\""))
				bw.write("<param name=\"FontInfo\" value=\"Microsoft Sans Serif,8,0,,\"  />\r\n");
			//Si on arrive à la fin du body on rajoute le footer
			else if (ligne.contains("</body>") && !isFooter) {
				bw.write("<?rh-placeholder type=\"footer\" ?>\r\n");
				bw.write("</body>");
			}
			//Sinon on réécrit
			else
				bw.write(ligne+"\r\n");
		}

		br.close();
		bw.close();

		Principale.fileMove(tmp, f);
		tmp.delete();
	}

	public ArrayList<File> getHtmlFiles() {
		return htmlFiles;
	}

	public void setHtmlFiles(ArrayList<File> htmlFiles) {
		this.htmlFiles = htmlFiles;
	}

	public String getPage() {
		return page;
	}

	public void setPage(String page) {
		this.page = page;
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
		// Ne rien faire
	}

	public String getPagePath() {
		return pagePath;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}
}
