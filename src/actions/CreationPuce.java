package actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Observable;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import interfaces.Action;
import interfaces.LancerAction;
import main.Principale;

public class CreationPuce extends Action {

	public CreationPuce(ArrayList<File> files) {
		super(files);
		intitule = "Création de puces";
	}

	@Override
	public void parametrer() {

	}

	@Override
	protected Document applyStyle(Document doc) throws IOException {
		// TODO Auto-generated method stub
		Elements style = doc.select("p");
		System.out.println(style.size());
		for (int i = 0; i < style.size(); i++) {
			Element element = style.get(i);
			System.out.println(element.text().toString());
			if (element.text().startsWith("Ø") || element.text().startsWith("§") || 
					element.text().startsWith("-") || element.text().startsWith("•") || element.text().startsWith("·")){
				String puce = element.text().substring(0,1);
				while (element != null && (element.text().startsWith(puce)) ){
					System.out.println(i);
					element.tagName("li");
					element.text(element.text().replace(puce, ""));
					i++;
					element = i < style.size() ? style.get(i) : null;
				}
			}
		}
		String html = doc.html();
		String[] lignes = html.split("\n");
		System.out.println("split length : "+lignes.length);
		boolean isList = false, isUl = false;
		for (int i = 0; i < lignes.length; i++) {
			if (lignes[i].contains("<ul>"))
				isUl = true;
			if ((lignes[i].contains("<li>") || lignes[i].contains("<li ")) && !isList && !isUl) {
				lignes[i] = "<ul>"+lignes[i];
				isList = true;
			}
			else if (!(lignes[i].contains("<li>") || lignes[i].contains("<li ")) && isList && !isUl){
				lignes[i] = lignes[i] + "</ul>";
				isList = false;
			}
			
			if (lignes[i].contains("</ul>"))
				isUl = false;
		}
		html = "";
		for (String string : lignes) {
			html += string + "\n";
		}
		doc = Jsoup.parse(html, "utf-8");
		return doc;
	}
}
