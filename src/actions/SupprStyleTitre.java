package actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import interfaces.Action;

public class SupprStyleTitre extends Action{

	public SupprStyleTitre(ArrayList<File> files) {
		super(files);
		intitule = "Suppression des styles additionnels des titres";
		messageFin = "Fin de la suppression des styles pour les titres";
	}

	@Override
	protected Document applyStyle(Document doc) throws IOException {
		Elements titres = doc.select("h1");
		titres.addAll(doc.select("h2"));
		titres.addAll(doc.select("h3"));
		titres.addAll(doc.select("h4"));
		titres.addAll(doc.select("h5"));
		
		for (Element element : titres) {
			for (Attribute a: element.attributes()) 
				element.removeAttr(a.getKey());
			for (String s : element.classNames())
				element.removeClass(s);
			element.text(element.text());
		}
		return doc;
	}

	@Override
	public void parametrer() {
		
	}

}
