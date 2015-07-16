package actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JOptionPane;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import interfaces.Action;

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
		Elements pp = doc.select("[name=template]");
		String html = doc.html();
		boolean isHeader = false, isFooter = false, isPP = false;
		if (html.contains("placeholder type=\"header\""))
			isHeader = true;
		if (html.contains("placeholder type=\"footer\""))
			isFooter = true;
		if (pp.size() > 0)
			isPP = true;
		System.out.println("pp : "+isPP+", header : "+isHeader+", footer : "+isFooter);
		if (isPP)
			pp.first().attr("content", pagePath);
		else {
			Elements meta = doc.select("meta");
			int index = meta.size();
			Element e = meta.first().clone();
			for (Attribute a: e.attributes()) {
				e.removeAttr(a.getKey());
			}
			e.attr("name", "template");
			e.attr("content", pagePath);
			System.out.println(e.toString());
			Elements head = doc.select("head");
			Elements list = new Elements(e);
			head.first().insertChildren(index, list);
		}
		if (!isFooter)
			doc.select("body").append("<!--?rh-placeholder type=\"footer\" ?--> ");
		if (!isHeader)
			doc.select("body").prepend("<!--?rh-placeholder type=\"header\" ?--> ");
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
