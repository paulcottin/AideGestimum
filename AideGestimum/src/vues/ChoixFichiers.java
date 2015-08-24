package vues;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import interfaces.NeedSelectionFichiers;

public class ChoixFichiers extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ListeFichier listeFichier;
	private JButton ok, annuler;
	private JFrame choixFrame;
	private ArrayList<File> files, principaleFiles;
	private NeedSelectionFichiers lancerAction;
	private Thread th;
	private boolean finish;

	public ChoixFichiers(ArrayList<File> files) {
		super();
		this.principaleFiles = new ArrayList<File>();
		this.principaleFiles.addAll(files);
		this.files = new ArrayList<File>();
		this.th = null;
		this.lancerAction = null;
		this.finish = false;
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		init();
	}
	
	public ChoixFichiers(ArrayList<File> files, NeedSelectionFichiers lancerAction) {
		super();
		this.files = new ArrayList<File>();
		this.principaleFiles = new ArrayList<File>();
		this.principaleFiles.addAll(files);
		this.lancerAction = lancerAction;
		this.th = new Thread(lancerAction);
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		init();
	}
	
	private void init(){
		this.listeFichier = new ListeFichier(principaleFiles);
		this.add(listeFichier);
		this.ok = new JButton("Valider");
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getSelectedLines(listeFichier);
				if (lancerAction != null) {
					lancerAction.fichiersSelectionnes(files);
					th.start();
					@SuppressWarnings("unused")
					ProgressBar bar = new ProgressBar(lancerAction);
				}else
					finish = true;
				choixFrame.dispose();
			}
		});


		this.choixFrame = new JFrame("Choix des fichiers");
		choixFrame.setVisible(true);
		choixFrame.setSize(1200, 600);
		choixFrame.setLocationRelativeTo(null);

		annuler = new JButton("Annuler");
		annuler.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				choixFrame.dispose();
			}
		});

		JPanel boutons = new JPanel();
		boutons.add(ok);
		boutons.add(annuler);

		this.add(boutons);
		choixFrame.setContentPane(this);
	}


	private void getSelectedLines(ListeFichier listeFichier){
		int[] index = listeFichier.getTable().getSelectedRows();
		for (int i : index) {
			files.add(new File((String) listeFichier.getTable().getModel().getValueAt(i, 0)));
		}
	}

	public ArrayList<File> getFiles() {
		return files;
	}

	public void setFiles(ArrayList<File> files) {
		this.files = files;
	}

	public boolean isFinish() {
		return finish;
	}

	public void setFinish(boolean finish) {
		this.finish = finish;
	}

}
