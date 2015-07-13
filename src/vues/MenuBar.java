package vues;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import controleurs.ExporterCSV;
import controleurs.Lancer;
import controleurs.ModifierNomFichiers;
import controleurs.Quitter;
import interfaces.LancerAction;
import main.Principale;



/**
 * Barre de menu
 * @author paul
 *
 */
public class MenuBar extends JMenuBar implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Principale p;

	private JMenu fichier, actions;
	private JMenuItem quitter, enregistrer, modifNomFichiers, rechercheCSS, remplacerTitre;
	
	public MenuBar(Principale p) {
		super();
		this.p = p;
		this.p.addObserver(this);
		init();
		construct();
	}
	
	private void init(){
		fichier = new JMenu("Fichier");
		quitter = new JMenuItem("Quitter");
		quitter.addActionListener(new Quitter());
		
		enregistrer = new JMenuItem("Exporter la liste des fichiers");
		enregistrer.addActionListener(new ExporterCSV(p));
		
		actions = new JMenu("Actions");
		modifNomFichiers = new JMenuItem("Modifier les noms de fichiers/dossiers");
		modifNomFichiers.addActionListener(new ModifierNomFichiers(p));
		modifNomFichiers.setEnabled(false);
		rechercheCSS = new JMenuItem("Recherche d'un CSS en fonction d'un nom de classe");
		rechercheCSS.addActionListener(new controleurs.SearchCSS(p.getFiles()));
		remplacerTitre = new JMenuItem("Remplacer les titres");
		remplacerTitre.addActionListener(new Lancer(p.getTitre(), p, Lancer.SELECTED_LINES));
	}
	
	private void construct(){
		fichier.add(enregistrer);
		fichier.addSeparator();
		fichier.add(quitter);
		
		actions.add(modifNomFichiers);
		actions.add(rechercheCSS);
		actions.add(remplacerTitre);
		
		this.add(fichier);
		this.add(actions);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
	}

}