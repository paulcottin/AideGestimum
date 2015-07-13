package vues;

import interfaces.LongTask;

import java.util.Observable;
import java.util.Observer;

import javax.swing.JFrame;
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

	public ProgressBar(LongTask task) {
		super("Modification en cours...");
		this.task = task;
		((Observable) this.task).addObserver(this);
		this.setSize(300, 50);
		this.setLocationRelativeTo(null);
		this.firstTime = true;
		bar = new JProgressBar();
		this.add(bar);
		bar.setIndeterminate(true);
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
	}
}