package actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import exceptions.FichierNonTrouve;
import interfaces.Action;
import main.Principale;

public class ChoixPagePrincipale extends Action {

	private String page, pagePath;

	public ChoixPagePrincipale(ArrayList<File> files) {
		super(files);
		messageFin = "Mise à jour de la page principale finie";
		intitule = "Choix de la page principale";
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

	@Override
	protected Document applyStyle(Document doc) throws IOException {
		Elements h = doc.select("[type=header]");
		Elements f = doc.select("[type=footer]");
		Elements pp = doc.select("[name=template]");
		boolean isHeader = false, isFooter = false, isPP = false;
		if (h.size() > 0)
			isHeader = true;
		if (f.size() > 0)
			isFooter = true;
		if (pp.size() > 0)
			isPP = true;
		System.out.println("header : "+isHeader+", footer : "+isFooter+", pp : "+isPP);
		if (isPP)
			pp.first().attr("content", pagePath);
		else {
			Elements meta = doc.select("meta");
			int index = meta.size() + 1;
			Element e = meta.first().clone();
			for (Attribute a: e.attributes()) {
				e.removeAttr(a.getKey());
			}
			e.attr("name", "template");
			e.attr("content", pagePath);
			doc.insertChildren(index, meta);
		}
		return doc;
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

	public String getPagePath() {
		return pagePath;
	}

	public void setPagePath(String pagePath) {
		this.pagePath = pagePath;
	}
}
