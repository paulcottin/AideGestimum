package actions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import interfaces.Action;

public class ColorationPuces extends Action {

	public ColorationPuces(ArrayList<File> files) {
		super(files);
		intitule = "Colorer les puces";
		messageFin = "Coloration des puces finies";
	}
	
	@Override
	public void parametrer() {

	}	

	@Override
	protected Document applyStyle(Document doc) throws IOException {
		String couleur = getCouleur(doc);
		
		Elements puces = doc.select("li");
		
		for (Element element : puces) {
			for (Attribute a : element.attributes()) {
				element.removeAttr(a.getKey());
			}
			element.attr("style", "color: "+couleur);
			if (isCleannable(element)) {
				element.html("<p>"+element.text()+"</p>");
			}
		}
		
		return doc;
	}
	
	private String getCouleur(Document doc) throws IOException {
		Elements pp = doc.select("meta[name=template]");
		if (pp.size() > 0){
			String path = getAbsolutePathPP(pp.first().attr("content"));
			Document d = Jsoup.parse(new File(path), "utf-8");
			Elements couleur = d.select("div");
			Element c = couleur.first();
			String l = c.toString();
			int deb = l.indexOf("<div style=\"background-color: ") + "<div style=\"background-color: ".length();
			int fin = l.substring(deb+1).indexOf(";")+1+deb;
			return l.substring(deb, fin);
		}else
			return null;
	}

	/**
	 * Récupère le path d'une page principale en fonction de son nom ou un path
	 * @param path
	 * @return
	 */
	private String getAbsolutePathPP(String path){
		//Si un path est donné en paramètre on récupère le nom
		if (path.contains("\\")) {
			Pattern p = Pattern.compile("\\\\");
			String[] t = path.split(p.pattern());
			path = t[t.length-1];
		}
		//On renvoi le chemin absolu
		for (File file : ppFiles) {
			if (file.getName().equals(path)) {
				return file.getAbsolutePath();
			}
		}
		return null;
	}
}
