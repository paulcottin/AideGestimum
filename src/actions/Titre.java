package actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import interfaces.Action;

public class Titre extends Action {
	
	public Titre(ArrayList<File> files) {
		super(files);
		intitule = "Création des titres";
		messageFin = "Titres créés";
	}

	@Override
	public void parametrer() {
		
	}
	
	@Override
	protected Document applyStyle(Document doc) throws IOException {
		Elements titres = doc.select("[style*=border-bottom]");
		for (Element element : titres) {
			element.tagName("h1");
			for (Attribute a : element.attributes()) {
				element.removeAttr(a.getKey());
			}
			element.text(element.text());
		}
		return doc;
	}

}
