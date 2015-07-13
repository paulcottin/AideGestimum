package vues;

import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controleurs.Lancer;
import controleurs.ParametrerScript;
import main.Principale;

public class Fenetre extends JFrame implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Principale principale;
	JLabel applicationStyle, choixFeuilleStyle, choixPagePrincipale, supprimerBalise, association, colorationPuces, changementStyle, script, creationPuce, creerLien;
	JButton applicationStyle_l, choixFeuilleStyle_l, choixPagePrincipale_l, supprimerBalise_l, association_l, colorationPuces_l, changementStyle_l, creationPuce_l, creerLien_l;
	JButton applicationStyle_all, choixFeuilleStyle_all, choixPagePincipale_all, supprimerBalise_all, colorationPuces_all, changementStyle_all, script_all, creationPuce_all, creerLien_all;
	JButton selectAll;
	
	ListeFichier listeFichier;
	
	public Fenetre(Principale principale) {
		super("Assistant de migration d'aide");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.principale= principale;
		this.principale.addObserver(this);
		this.listeFichier = new ListeFichier(principale.getFiles());
		
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		initWin();
		createWin();
		
		this.setVisible(true);
	}
	
	private void initWin(){
		this.setSize(500, 500);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setJMenuBar(new vues.MenuBar(principale));
		
		applicationStyle = new JLabel("Application d'un style particulier à un mot");
		applicationStyle_l = new JButton("Sélection");
		applicationStyle_l.addActionListener(new Lancer(principale.getStyle(), principale, Lancer.SELECTED_LINES));
		applicationStyle_all = new JButton("Tout");
		applicationStyle_all.addActionListener(new Lancer(principale.getStyle(), principale));
		
		choixFeuilleStyle = new JLabel("Choix d'une feuille de style");
		choixFeuilleStyle_l = new JButton("Sélection");
		choixFeuilleStyle_l.addActionListener(new Lancer(principale.getChoixFeuilleStyle(), principale, Lancer.SELECTED_LINES));
		choixFeuilleStyle_all = new JButton("Tout");
		choixFeuilleStyle_all.addActionListener(new Lancer(principale.getChoixFeuilleStyle(), principale));
		
		choixPagePrincipale = new JLabel("Choix d'une page principale");
		choixPagePrincipale_l = new JButton("Sélection");
		choixPagePrincipale_l.addActionListener(new Lancer(principale.getChoixPagePrincipale(), principale, Lancer.SELECTED_LINES));
		choixPagePincipale_all = new JButton("Tout");
		choixPagePincipale_all.addActionListener(new Lancer(principale.getChoixPagePrincipale(), principale));
		
		supprimerBalise = new JLabel("Supprimer une balise");
		supprimerBalise_l = new JButton("Sélection");
		supprimerBalise_l.addActionListener(new Lancer(principale.getSupprimerBalise(), principale, Lancer.SELECTED_LINES));
		supprimerBalise_all = new JButton("Tout");
		supprimerBalise_all.addActionListener(new Lancer(principale.getSupprimerBalise(), principale));
		
		association = new JLabel("Associer automatiquement les pages principales aux rubriques");
		association_l = new JButton("Lancer");
		association_l.addActionListener(new Lancer(principale.getAssocAuto(), principale));
		
		colorationPuces = new JLabel("Colorer les puces en fonction du bandeau");
		colorationPuces_l = new JButton("Sélection");
		colorationPuces_l.addActionListener(new Lancer(principale.getColorationPuces(), principale, Lancer.SELECTED_LINES));
		colorationPuces_all = new JButton("Tout");
		colorationPuces_all.addActionListener(new Lancer(principale.getColorationPuces(), principale));
		
		changementStyle = new JLabel("Changer d'un style à un autre");
		changementStyle_l = new JButton("Sélection");
		changementStyle_l.addActionListener(new Lancer(principale.getChangerStyle(), principale, Lancer.SELECTED_LINES));
		changementStyle_all = new JButton("Tout");
		changementStyle_all.addActionListener(new Lancer(principale.getChangerStyle(), principale));
		
		script = new JLabel("Paramétrer un script");
		script_all = new JButton("Paramétrer");
		script_all.addActionListener(new ParametrerScript(principale.getScript()));
		
		creationPuce = new JLabel("Création de puces");
		creationPuce_l = new JButton("Sélection");
		creationPuce_l.addActionListener(new Lancer(principale.getCreationPuce(), principale, Lancer.SELECTED_LINES));
		creationPuce_all = new JButton("Tous");
		creationPuce_all.addActionListener(new Lancer(principale.getCreationPuce(), principale));
		
		creerLien = new JLabel("Créer les liens");
		creerLien_l = new JButton("Sélection");
		creerLien_l.addActionListener(new Lancer(principale.getLien(), principale, Lancer.SELECTED_LINES));
		creerLien_all = new JButton("Tous");
		creerLien_all.addActionListener(new Lancer(principale.getLien(), principale));		
	}
	
	private void createWin(){
		JPanel style = new JPanel();
		style.add(applicationStyle);
		style.add(applicationStyle_l);
		style.add(applicationStyle_all);
		
		JPanel choixFeuille = new JPanel();
		choixFeuille.add(choixFeuilleStyle);
		choixFeuille.add(choixFeuilleStyle_l);
		choixFeuille.add(choixFeuilleStyle_all);
		
		JPanel choixPP = new JPanel();
		choixPP.add(choixPagePrincipale);
		choixPP.add(choixPagePrincipale_l);
		choixPP.add(choixPagePincipale_all);
		
		JPanel suppBalise = new JPanel();
		suppBalise.add(supprimerBalise);
		suppBalise.add(supprimerBalise_l);
		suppBalise.add(supprimerBalise_all);
		
		JPanel assocAuto = new JPanel();
		assocAuto.add(association);
		assocAuto.add(association_l);
		
		JPanel coloPuces = new JPanel();
		coloPuces.add(colorationPuces);
		coloPuces.add(colorationPuces_l);
		coloPuces.add(colorationPuces_all);
		
		JPanel changerS = new JPanel();
		changerS.add(changementStyle);
		changerS.add(changementStyle_l);
		changerS.add(changementStyle_all);
		
		JPanel scriptP = new JPanel();
		scriptP.add(script);
		scriptP.add(script_all);
		
		JPanel creationPuce = new JPanel();
		creationPuce.add(this.creationPuce);
		creationPuce.add(creationPuce_l);
		creationPuce.add(creationPuce_all);
		
		JPanel creerLien = new JPanel();
		creerLien.add(this.creerLien);
		creerLien.add(creerLien_l);
		creerLien.add(creerLien_all);
		
		this.getContentPane().add(style);
		this.getContentPane().add(choixFeuille);
		this.getContentPane().add(choixPP);
		this.getContentPane().add(suppBalise);
		this.getContentPane().add(assocAuto);
		this.getContentPane().add(coloPuces);
		this.getContentPane().add(changerS);
		this.getContentPane().add(scriptP);
		this.getContentPane().add(creationPuce);
		this.getContentPane().add(creerLien);
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		
	}
}
