package vues;

import interfaces.LongTask;

import java.util.Observable;
import java.util.Observer;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

/**
 * Barre de progression tâches un peu longues
 * @author paul
 *
 */
public class ProgressBar extends JFrame implements Observer{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JProgressBar bar;
	private LongTask task;
	private boolean firstTime;
	private JLabel fichierEnCours;
	private String fichier;

	public ProgressBar(LongTask task) {
		super("Modification en cours...");
		this.task = task;
		((Observable) this.task).addObserver(this);
		this.setSize(550, 70);
		this.setLocationRelativeTo(null);
		this.firstTime = true;
		this.fichier = "";
		this.fichierEnCours = new JLabel("Traitement de "+fichier+"...");
		this.getContentPane().setLayout(new BoxLayout(this.getContentPane(), BoxLayout.PAGE_AXIS));
		this.getContentPane().add(fichierEnCours);
		bar = new JProgressBar();
		this.add(bar);
		bar.setIndeterminate(true);
		this.getContentPane().add(bar);
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!task.isRunning()) {
			if (firstTime) {
				firstTime = false;
				task.onDispose();
			}
			this.dispose();
		}else 
			this.setVisible(true);
		fichier = task.getFichierTraitement();
		fichierEnCours.setText("Traitement de "+fichier+"...");
	}
}