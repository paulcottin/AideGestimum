package utilitaires;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import actions.Style;

public class StyleDialogue extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private StyleDialogueInfo Info = new StyleDialogueInfo();
	@SuppressWarnings("unused")
	private boolean sendData;
	private JLabel motLabel;
	@SuppressWarnings("unused")
	private JComboBox<String> styles;
	private ArrayList<File> cssFiles;
	private String cssFilePath;
	private Style style_class;

	public StyleDialogue(JFrame parent, String title, boolean modal, ArrayList<File> files, Style style){
		super(parent, title, modal);
		this.cssFiles = new ArrayList<File>();
		for (File file : files) {
			if (file.getAbsolutePath().endsWith(".css")) {
				cssFiles.add(file);
			}
		}
		this.style_class = style;
		this.setSize(550, 270);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		getCSSFile();
		this.initComponent();
	}

	public StyleDialogueInfo showDialog(){
		this.sendData = false;
		this.setVisible(true);      
		return this.Info;      
	}

	private void initComponent(){
		//Le mot
		JPanel panMot = new JPanel();
		panMot.setBackground(Color.white);
		panMot.setPreferredSize(new Dimension(220, 60));
		JTextField mot = new JTextField();
		mot.setPreferredSize(new Dimension(100, 25));
		motLabel = new JLabel("Quel mot (attention à la casse) :");
		panMot.add(motLabel);
		panMot.add(mot);

		//Le style
		JPanel panStyle = new JPanel();
		panStyle.setBackground(Color.white);
		panStyle.setPreferredSize(new Dimension(220, 60));
		JComboBox<String> style = new JComboBox<String>();
		for (String s : getCSSclasses()) {
			style.addItem(s);
		}
		JLabel styleLabel = new JLabel("Styles : ");
		panStyle.add(styleLabel);
		panStyle.add(style);

		JPanel content = new JPanel();
		content.setBackground(Color.white);
		content.add(panMot);
		content.add(panStyle);

		JPanel control = new JPanel();
		JButton okBouton = new JButton("OK");

		okBouton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {  
				if (mot.getText() != "") {
					Info = new StyleDialogueInfo(mot.getText(), (String)style.getSelectedItem());
					setVisible(false);
				}else
					Info = null;
			}    
		});

		JButton cancelBouton = new JButton("Annuler");
		cancelBouton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				Info = null;
			}      
		});

		control.add(okBouton);
		control.add(cancelBouton);

		this.getContentPane().add(content, BorderLayout.CENTER);
		this.getContentPane().add(control, BorderLayout.SOUTH);
	}  

	private ArrayList<String> getCSSclasses(){
		ArrayList<String> reponse = new ArrayList<String>();


		File file = new File(cssFilePath);

		try {
			BufferedReader br = new BufferedReader(new FileReader(file));

			String ligne = "";
			while((ligne = br.readLine()) != null){
				if (ligne.startsWith("span.")) {
					int deb = ligne.indexOf("span.") + "span.".length();
					int fin = ligne.indexOf("{");
					reponse.add(ligne.substring(deb, fin));
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();

		}
		return reponse;
	}

	private void getCSSFile(){
		cssFilePath = "";
		String[] cssFiles = new String[this.cssFiles.size()];
		for (int i = 0; i < this.cssFiles.size(); i++) {
			cssFiles[i] = this.cssFiles.get(i).getName();
		}

		cssFilePath =	(String) JOptionPane.showInputDialog(null, 
				"Choisir la feuille de style",
				"Paramétrage",
				JOptionPane.QUESTION_MESSAGE,
				null,
				cssFiles, cssFiles[0]);
		
		for (File file: this.cssFiles) {
			if (file.getName().equals(cssFilePath))
				cssFilePath = file.getAbsolutePath();
		}
	}

	public StyleDialogueInfo getInfo() {
		return Info;
	}

	public void setInfo(StyleDialogueInfo info) {
		Info = info;
	}

	public String getCssFilePath() {
		return cssFilePath;
	}

	public void setCssFilePath(String cssFilePath) {
		this.cssFilePath = cssFilePath;
	}

}
