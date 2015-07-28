package vues;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import controleurs.Lancer;
import controleurs.ParametrerScript;
import interfaces.Action;
import main.Principale;

public class Fenetre extends JFrame implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Principale principale;
	private ArrayList<JLabel> labels;
	private ArrayList<JButton> selects, alls;
	
	JLabel script, assocAuto;
	JButton script_all, assocAuto_all;
	
	ListeFichier listeFichier;
	
	public Fenetre(Principale principale) {
		super("Assistant de migration d'aide");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.principale= principale;
		this.principale.addObserver(this);
		this.listeFichier = new ListeFichier(principale.getFiles());
		this.labels = new ArrayList<JLabel>();
		this.selects = new ArrayList<JButton>();
		this.alls = new ArrayList<JButton>();
		
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		initWin();
		createWin();
		
		this.setVisible(true);
	}
	
	private void initWin(){
		this.setSize(500, 700);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setLocationRelativeTo(null);
		this.setJMenuBar(new vues.MenuBar(principale));
		
		script = new JLabel("Paramétrer un script");
		script_all = new JButton("Paramétrer");
		script_all.addActionListener(new ParametrerScript(principale.getScript()));
		
		assocAuto = new JLabel("Association automatique de page principales");
		assocAuto_all = new JButton("Lancer");
		assocAuto_all.addActionListener(new Lancer(principale.getAssociationAuto(), principale));
	}
	

	private void createWin(){
		for (Action action : principale.getScripts()) {
			labels.add(new JLabel(action.getIntitule()));
			JButton b = new JButton("Sélection");
			b.addActionListener(new Lancer(action, principale, Lancer.SELECTED_LINES));
			selects.add(b);
			JButton a = new JButton("Tous");
			a.addActionListener(new Lancer(action, principale));
			alls.add(a);
		}
		
		for (int i = 0; i < labels.size(); i++) {
			JPanel p = new JPanel();
			p.add(labels.get(i));
			p.add(selects.get(i));
			p.add(alls.get(i));
			this.getContentPane().add(p);
		}
		
		JPanel scr = new JPanel();
		scr.add(script);
		scr.add(script_all);
		
		JPanel assoc = new JPanel();
		assoc.add(assocAuto);
		assoc.add(assocAuto_all);
		
		this.getContentPane().add(scr);
		this.getContentPane().add(assoc);
	}
	
	@Override
	public void update(Observable arg0, Object arg1) {
		
	}
}
