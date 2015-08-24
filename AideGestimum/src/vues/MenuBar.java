package vues;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import controleurs.AfficheAide;
import controleurs.ConvertImg;
import controleurs.ExportTaille;
import controleurs.ExporterCSV;
import controleurs.HelpCopieEcran;
import controleurs.ModifID;
import controleurs.ModifierNomFichiers;
import controleurs.NoHelp;
import controleurs.Quitter;
import controleurs.SupprimerImageTrop;
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
	private boolean isRoboHelpProject;

	private JMenu fichier, actions, aide;
	private JMenuItem quitter, enregistrer, modifNomFichiers, rechercheCSS, voirAide, exportTaille, noHelp,
						modifID, convertImg, helpCopieEcran, supprimerImgEnTrop;
	
	public MenuBar(Principale p, boolean isRoboHelpProject) {
		super();
		this.p = p;
		this.p.addObserver(this);
		this.isRoboHelpProject = isRoboHelpProject;
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
		if (!isRoboHelpProject) rechercheCSS.setEnabled(false);
		
		noHelp = new JMenuItem("Check des fenêtres qui n'ont pas de page d'aide");
		noHelp.addActionListener(new NoHelp());
		if (!isRoboHelpProject) noHelp.setEnabled(false);
		
		modifID = new JMenuItem("Modification des id d'aide des rubriques");
		modifID.addActionListener(new ModifID(p.getFiles()));
		if (!isRoboHelpProject) modifID.setEnabled(false);
		
		convertImg = new JMenuItem("Convertir les images png en jpeg");
		convertImg.addActionListener(new ConvertImg(p.getFiles()));
		
		supprimerImgEnTrop = new JMenuItem("Supprimer les images qui ne sont plus utilisées dans le projet");
		supprimerImgEnTrop.addActionListener(new SupprimerImageTrop(p.getFiles()));
		if (!isRoboHelpProject) supprimerImgEnTrop.setEnabled(false);
		
		aide = new JMenu("?");
		voirAide = new JMenuItem("Aide");
		voirAide.addActionListener(new AfficheAide());
		
		helpCopieEcran = new JMenuItem("Aide pour les copies d'écran");
		helpCopieEcran.addActionListener(new HelpCopieEcran(p.getFiles()));
		
	}
	
	private void construct(){
		fichier.add(enregistrer);
		fichier.add(exportTaille);
		fichier.addSeparator();
		fichier.add(quitter);
		
		actions.add(modifNomFichiers);
		actions.add(rechercheCSS);
		actions.add(noHelp);
		actions.add(modifID);
		actions.add(convertImg);
		actions.add(helpCopieEcran);
		actions.add(supprimerImgEnTrop);
		
		aide.add(voirAide);
		
		this.add(fichier);
		this.add(actions);
		this.add(aide);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
	}

}