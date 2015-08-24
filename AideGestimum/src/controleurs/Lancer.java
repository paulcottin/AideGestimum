package controleurs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import interfaces.LancerAction;
import main.Principale;
import vues.ChoixFichiers;
import vues.ProgressBar;

public class Lancer implements ActionListener{
	public static int ALL = 0;
	public static int SELECTED_LINES = 1;


	private LancerAction p;
	private int choix;
	private Principale principale;
	private Thread th;

	public Lancer(LancerAction p, Principale principale){
		this.p = p;
		this.th = new Thread(p);
		this.choix = ALL;
		this.principale = principale;
	}

	public Lancer(LancerAction p, Principale principale, int choix){
		this.p = p;
		this.th = new Thread(p);
		this.choix = SELECTED_LINES;
		this.principale = principale;
	}


	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (choix == ALL){
			p.reloadFiles(principale.listerRepertoire(principale.getTopics()));
			th.start();
			@SuppressWarnings("unused")
			ProgressBar bar = new ProgressBar(p);
		}
		else if (choix == SELECTED_LINES){
			@SuppressWarnings("unused")
			ChoixFichiers cf = new ChoixFichiers(principale.getFiles(), p);

		}
	}

}
