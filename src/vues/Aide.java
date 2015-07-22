package vues;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import exceptions.AideException;

public class Aide extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String aideHTML;
	private JEditorPane pane;
	
	public Aide() throws AideException {
		aideHTML = "Erreur d'affichage...\nVérifiez que le fichier aide.html est présent dans le dossier du .jar et qu'il n'est pas vide.";
		try {
			lireAide();
		} catch (IOException e) {
			throw new AideException("Erreur lors de la lecture du fichier aide.html");
		}
		System.out.println(aideHTML);
		pane = new JEditorPane();
		initAide();
	}
	
	private void initAide(){
		this.setVisible(true);
		this.setSize(1200, 700);
		this.setLocationRelativeTo(null);
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		pane.setContentType("text/html");
		pane.setText(aideHTML);
		pane.setEditable(false);
		this.setContentPane(new JScrollPane(pane));
	}
	
	private void lireAide() throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("aide.html")), "utf-8"));
		String ligne = "";
		aideHTML = "";
		while ((ligne = br.readLine()) != null) {
			aideHTML += ligne;
		}
		
		br.close();
	}

}
