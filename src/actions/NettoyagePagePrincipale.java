package actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import interfaces.Action;

public class NettoyagePagePrincipale extends Action{

	public NettoyagePagePrincipale(ArrayList<File> files) {
		super(files);
		intitule = "Nettoyage des pages principales";
		messageFin = "Nettoyage des pages principales terminé";
	}

	@Override
	protected Document applyStyle(Document doc) throws IOException {
		Elements pps = doc.select("meta[name=template][content=null]");
		
		for (Element element : pps) {
			element.remove();
		}
		return doc;
	}

	@Override
	public void parametrer() {

	}

}
