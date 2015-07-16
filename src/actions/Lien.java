package actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import interfaces.Action;

public class Lien extends Action{

	public Lien(ArrayList<File> files) {
		super(files);
		intitule = "Donner un style aux liens";
		messageFin = "Application du style \"Lien\" aux liens fini";
	}

	@Override
	protected Document applyStyle(Document doc) throws IOException {
		Elements links = doc.select("a[href]");
		
		for (Element element : links) {
			element.addClass("Lien");
		}
		return doc;
	}

	@Override
	public void parametrer() {
		// Ne rien faire
	}
}
