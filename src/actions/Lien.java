package actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import interfaces.Action;

public class Lien extends Action{

	String classe;
	
	public Lien(ArrayList<File> files) {
		super(files);
		intitule = "Donner un style aux liens";
		messageFin = "Application du style \"Lien\" aux liens fini";
		classe = "Lien";
	}

	@Override
	protected Document applyStyle(Document doc) throws IOException {
		Elements links = doc.select("a[href]");
		
		for (Element element : links) {
			for (Attribute a : element.attributes()) 
				element.removeAttr(a.getKey());
			for (String s : element.classNames()) {
				element.removeClass(s);
			}
			element.addClass(classe);
		}
		return doc;
	}

	@Override
	public void parametrer() {
		String c = cssClass(cssFile("Paramétrage", "Veuillez donner la feuille de style"), "Paramétrage", "Quelle classe pour les liens ?");
		if (c != null)
			classe = c;
	}
}
