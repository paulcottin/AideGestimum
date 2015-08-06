package vues;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import controleurs.AfficheAide;
import controleurs.ExportTaille;
import controleurs.ExporterCSV;
import controleurs.ModifierNomFichiers;
import controleurs.NoHelp;
import controleurs.Quitter;
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

	private JMenu fichier, actions, aide;
	private JMenuItem quitter, enregistrer, modifNomFichiers, rechercheCSS, voirAide, exportTaille, noHelp;
	
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
		
		enregistrer = new JMenuItem("Exporter la liste des fichiers du projet RoboHelp");
		enregistrer.addActionListener(new ExporterCSV(p));
		exportTaille = new JMenuItem("Exporter la totalité des fichiers avec leurs tailles");
		exportTaille.addActionListener(new ExportTaille(p.getFiles()));
		
		actions = new JMenu("Actions");
		modifNomFichiers = new JMenuItem("Modifier les noms de fichiers/dossiers");
		modifNomFichiers.addActionListener(new ModifierNomFichiers(p));
		modifNomFichiers.setEnabled(false);
		rechercheCSS = new JMenuItem("Recherche d'un CSS en fonction d'un nom de classe");
		rechercheCSS.addActionListener(new controleurs.SearchCSS(p.getFiles()));
		
		noHelp = new JMenuItem("Check des fenêtres qui n'ont pas de page d'aide");
		noHelp.addActionListener(new NoHelp());
		
		aide = new JMenu("?");
		voirAide = new JMenuItem("Aide");
		voirAide.addActionListener(new AfficheAide());
	}
	
	private void construct(){
		fichier.add(enregistrer);
		fichier.add(exportTaille);
		fichier.addSeparator();
		fichier.add(quitter);
		
		actions.add(modifNomFichiers);
		actions.add(rechercheCSS);
		actions.add(noHelp);
		
		aide.add(voirAide);
		
		this.add(fichier);
		this.add(actions);
		this.add(aide);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
	}

}