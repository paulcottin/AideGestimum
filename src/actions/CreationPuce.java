package actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import interfaces.Action;

public class CreationPuce extends Action {

	public CreationPuce(ArrayList<File> files) {
		super(files);
		intitule = "Création de puces";
		messageFin = "Création des puces terminée";
	}

	@Override
	public void parametrer() {

	}

	@Override
	protected Document applyStyle(Document doc) throws IOException {
		// TODO Auto-generated method stub
		Elements style = doc.select("p");
		for (int i = 0; i < style.size(); i++) {
			Element element = style.get(i);
			if (element.text().startsWith("Ã˜") || element.text().startsWith("Â§") || 
					element.text().startsWith("-") || element.text().startsWith("•") || element.text().startsWith("Â·")){
				String puce = element.text().substring(0,element.text().toString().indexOf(" "));
				while (element != null && (element.text().startsWith(puce)) ){
					element.tagName("li");
					element.text(element.text().toString().replace(puce, ""));
					i++;
					element = i < style.size() ? style.get(i) : null;
				}
			}
		}
		String html = doc.html();
		String[] lignes = html.split("\n");
		boolean isList = false, isUl = false;
		for (int i = 0; i < lignes.length; i++) {
			if (lignes[i].contains("<ul>"))
				isUl = true;
			if ((lignes[i].contains("<li>") || lignes[i].contains("<li ")) && !isList && !isUl) {
				lignes[i] = "<ul>"+lignes[i];
				isList = true;
			}
			else if (!(lignes[i].contains("<li>") || lignes[i].contains("<li ")) && isList && !isUl){
				lignes[i] = "</ul>" + lignes[i];
				isList = false;
			}
			
			if (lignes[i].contains("</ul>"))
				isUl = false;
		}
		html = "";
		for (String string : lignes) {
			html += string + "\n";
		}
		doc = Jsoup.parse(html);
		return doc;
	}
}
